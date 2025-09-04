package com.zj.config;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于向量数据库的聊天记录管理器
 * 使用PgVectorStore进行聊天记录的向量化存储和智能检索
 * 支持基于用户ID的数据隔离和相关性搜索
 */
@Component
public class VectorStoreRetrieverMemory implements ChatMemory {
    
    private static final Logger logger = LoggerFactory.getLogger(VectorStoreRetrieverMemory.class);
    
    // 向量搜索的最大返回结果数
    private static final int MAX_SEARCH_RESULTS = 10;
    
    // 最低相似度阈值（0-1之间，数值越高要求越严格）
    private static final double SIMILARITY_THRESHOLD = 0.3;
    
    // 日期时间格式化器
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Resource
    private PgVectorStore pgVectorStore;

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (conversationId == null || messages == null || messages.isEmpty()) {
            logger.warn("会话ID或消息列表为空，跳过存储");
            return;
        }
        
        try {
            List<Document> documents = new ArrayList<>();
            String currentTime = LocalDateTime.now().format(DATE_FORMATTER);
            
            for (Message message : messages) {
                Document document = convertMessageToDocument(message, conversationId, currentTime);
                if (document != null) {
                    documents.add(document);
                }
            }
            
            if (!documents.isEmpty()) {
                // 批量存储到向量数据库
                pgVectorStore.accept(documents);
                logger.info("成功存储 {} 条消息到用户 {} 的向量数据库", documents.size(), conversationId);
            }
            
        } catch (Exception e) {
            logger.error("存储消息到向量数据库失败，会话ID: {}", conversationId, e);
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        if (conversationId == null) {
            logger.warn("会话ID为空，返回空消息列表");
            return new ArrayList<>();
        }
        
        try {
            // 构建搜索请求，只搜索当前用户的消息
            SearchRequest searchRequest = SearchRequest.builder()
                .topK(MAX_SEARCH_RESULTS)
                .similarityThreshold(SIMILARITY_THRESHOLD)
                .filterExpression(new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("user_id"),
                    new Filter.Value(conversationId)
                ))
                .build();
            
            // 从向量数据库检索相关消息
            List<Document> relevantDocuments = pgVectorStore.similaritySearch(searchRequest);
            
            // 将Document转换回Message，并按时间排序
            List<Message> messages = relevantDocuments.stream()
                .map(this::convertDocumentToMessage)
                .filter(Objects::nonNull)
                .sorted(this::compareMessagesByTime)
                .collect(Collectors.toList());
            
            logger.debug("为用户 {} 检索到 {} 条相关历史消息", conversationId, messages.size());
            
            // 如果有相关消息，添加一个系统消息作为上下文说明
            if (!messages.isEmpty()) {
                List<Message> result = new ArrayList<>();
                result.add(new SystemMessage("以下是与当前对话相关的历史消息上下文，供参考："));
                result.addAll(messages);
                return result;
            }
            
            return messages;
            
        } catch (Exception e) {
            logger.error("从向量数据库检索消息失败，会话ID: {}", conversationId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 基于查询文本智能检索相关历史记录
     * @param conversationId 用户会话ID
     * @param queryText 查询文本
     * @return 相关的历史消息列表
     */
    public List<Message> searchRelevantHistory(String conversationId, String queryText) {
        if (conversationId == null || queryText == null || queryText.trim().isEmpty()) {
            logger.warn("会话ID或查询文本为空，返回空消息列表");
            return new ArrayList<>();
        }
        
        try {
            // 构建基于内容相似性的搜索请求
            SearchRequest searchRequest = SearchRequest.builder()
                .query(queryText)
                .topK(MAX_SEARCH_RESULTS)
                .similarityThreshold(SIMILARITY_THRESHOLD)
                .filterExpression(new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("user_id"),
                    new Filter.Value(conversationId)
                ))
                .build();
            
            // 执行向量搜索
            List<Document> relevantDocuments = pgVectorStore.similaritySearch(searchRequest);
            
            // 转换并排序结果
            List<Message> messages = relevantDocuments.stream()
                .map(this::convertDocumentToMessage)
                .filter(Objects::nonNull)
                .sorted(this::compareMessagesByTime)
                .collect(Collectors.toList());
            
            logger.info("基于查询 '{}' 为用户 {} 找到 {} 条相关历史消息", queryText, conversationId, messages.size());
            return messages;
            
        } catch (Exception e) {
            logger.error("智能检索历史消息失败，会话ID: {}, 查询: {}", conversationId, queryText, e);
            return new ArrayList<>();
        }
    }

    @Override
    public void clear(String conversationId) {
        if (conversationId == null) {
            logger.warn("会话ID为空，无法清除数据");
            return;
        }
        
        try {
            // 注意：PgVectorStore没有提供按过滤条件删除的直接方法
            // 这里我们只记录日志，实际的清除操作可能需要直接操作数据库
            logger.info("请求清除用户 {} 的所有聊天记录。注意：需要手动清理向量数据库中的数据", conversationId);
            
            // TODO: 如果需要实现真正的清除功能，可能需要：
            // 1. 直接操作PostgreSQL数据库
            // 2. 或者在Document的metadata中添加删除标记
            // 3. 或者实现自定义的删除逻辑
            
        } catch (Exception e) {
            logger.error("清除用户 {} 的聊天记录时发生错误", conversationId, e);
        }
    }
    
    /**
     * 将Message转换为Document以便存储到向量数据库
     */
    private Document convertMessageToDocument(Message message, String conversationId, String timestamp) {
        try {
            String content = null;
            String messageType = null;
            
            if (message instanceof UserMessage userMessage) {
                content = userMessage.getText();
                messageType = "user";
            } else if (message instanceof AssistantMessage assistantMessage) {
                content = assistantMessage.getText();
                messageType = "assistant";
            } else if (message instanceof SystemMessage systemMessage) {
                content = systemMessage.getText();
                messageType = "system";
            }
            
            if (content == null || content.trim().isEmpty()) {
                return null;
            }
            
            // 构建文档内容，包含消息类型和内容
            String documentText = String.format("[%s]: %s", messageType, content);
            
            // 创建元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("user_id", conversationId);
            metadata.put("message_type", messageType);
            metadata.put("timestamp", timestamp);
            metadata.put("content_length", content.length());
            
            return new Document(documentText, metadata);
            
        } catch (Exception e) {
            logger.error("转换Message为Document时发生错误", e);
            return null;
        }
    }
    
    /**
     * 将Document转换回Message
     */
    private Message convertDocumentToMessage(Document document) {
        try {
            String text = document.getText();
            Map<String, Object> metadata = document.getMetadata();
            
            String messageType = (String) metadata.get("message_type");
            
            // 提取原始消息内容（去掉类型前缀）
            String content = text;
            if (text.startsWith("[" + messageType + "]: ")) {
                content = text.substring(("[" + messageType + "]: ").length());
            }
            
            return switch (messageType) {
                case "user" -> new UserMessage(content);
                case "assistant" -> new AssistantMessage(content);
                case "system" -> new SystemMessage(content);
                default -> {
                    logger.warn("未知的消息类型: {}", messageType);
                    yield new UserMessage(content);
                }
            };
            
        } catch (Exception e) {
            logger.error("转换Document为Message时发生错误", e);
            return null;
        }
    }
    
    /**
     * 按时间戳比较消息
     */
    private int compareMessagesByTime(Message m1, Message m2) {
        try {
            // 这里假设消息对象有某种方式获取时间戳
            // 由于Message接口没有直接的时间戳字段，我们使用消息内容的自然顺序
            return 0; // 保持原有顺序
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * 获取用户的消息统计信息
     */
    public Map<String, Object> getMessageStats(String conversationId) {
        if (conversationId == null) {
            return new HashMap<>();
        }
        
        try {
            // 搜索用户的所有消息
            SearchRequest searchRequest = SearchRequest.builder()
                .topK(1000) // 设置一个较大的数值来获取所有消息
                .filterExpression(new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("user_id"),
                    new Filter.Value(conversationId)
                ))
                .build();
            
            List<Document> allDocuments = pgVectorStore.similaritySearch(searchRequest);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("total_messages", allDocuments.size());
            
            // 按消息类型统计
            Map<String, Long> typeCount = allDocuments.stream()
                .collect(Collectors.groupingBy(
                    doc -> (String) doc.getMetadata().getOrDefault("message_type", "unknown"),
                    Collectors.counting()
                ));
            
            stats.put("message_types", typeCount);
            
            logger.debug("用户 {} 的消息统计: {}", conversationId, stats);
            return stats;
            
        } catch (Exception e) {
            logger.error("获取用户 {} 的消息统计失败", conversationId, e);
            return new HashMap<>();
        }
    }
    
    /**
     * 清理过期的聊天记录（需要配合定时任务使用）
     * @param conversationId 用户ID
     * @param daysToKeep 保留天数
     */
    public void cleanupOldMessages(String conversationId, int daysToKeep) {
        logger.info("清理用户 {} 超过 {} 天的聊天记录（需要实现具体的清理逻辑）", conversationId, daysToKeep);
        // TODO: 实现基于时间戳的消息清理逻辑
    }
}
