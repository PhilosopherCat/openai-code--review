package plus.gaga.middleware.controller;

import plus.gaga.middleware.controller.dto.ReviewRequest;
import plus.gaga.middleware.controller.dto.ReviewResponse;
import plus.gaga.middleware.service.ReviewApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewApiService reviewApiService;

    public ReviewController(ReviewApiService reviewApiService) {
        this.reviewApiService = reviewApiService;
    }

    /**
     * 发起代码评审
     * 
     * @param request 评审请求
     * @return 评审任务 ID 和初始状态
     */
    @PostMapping
    public ResponseEntity<ReviewResponse> submitReview(@RequestBody ReviewRequest request) {
        if ((request.getRepoUrl() == null || request.getRepoUrl().isEmpty())
                && (request.getDiffContent() == null || request.getDiffContent().isEmpty())) {
            return ResponseEntity.badRequest().body(ReviewResponse.builder()
                    .status("failed")
                    .errorMessage("repoUrl 或 diffContent 至少需要一个")
                    .build());
        }
        
        ReviewResponse response = reviewApiService.submitReview(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 查询评审结果
     * 
     * @param taskId 评审任务 ID
     * @return 评审结果
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<ReviewResponse> getReviewResult(@PathVariable String taskId) {
        ReviewResponse response = reviewApiService.getReviewResult(taskId);
        
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
