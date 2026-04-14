package plus.gaga.middleware.controller.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    
    /**
     * GitHub 仓库 URL
     * 例如：https://github.com/username/repo
     */
    private String repoUrl;
    
    /**
     * 分支名称
     * 例如：main、master、feature-xxx
     */
    private String branch;
    
    /**
     * GitHub Token (可选，私有仓库需要)
     */
    private String githubToken;
    
    /**
     * 代码 diff 内容 (可选，与 repoUrl 二选一)
     * 如果直接传入 diff 内容，则不需要 repoUrl
     */
    private String diffContent;
    
    /**
     * 是否发送微信通知
     */
    private Boolean enableWeixinNotify = false;
}
