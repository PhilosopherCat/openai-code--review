package plus.gaga.middleware.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    
    /**
     * 评审任务 ID
     */
    private String taskId;
    
    /**
     * 评审状态：pending, processing, success, failed
     */
    private String status;
    
    /**
     * 代码评分 (0-100)
     */
    private Integer score;
    
    /**
     * AI 评审内容 (Markdown 格式)
     */
    private String reviewContent;
    
    /**
     * 评审结果链接
     */
    private String reviewUrl;
    
    /**
     * 错误信息 (如果有)
     */
    private String errorMessage;
}
