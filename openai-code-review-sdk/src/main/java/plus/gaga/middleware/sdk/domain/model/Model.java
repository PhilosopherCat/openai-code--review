package plus.gaga.middleware.sdk.domain.model;

public enum Model {

    DEEPSEEK_CHAT("deepseek-chat",  "适用于对图像生成、图像描述、图像风格转换、图像修复、图像生成等场景");

    private final String code;
    private final String info;

    Model(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

}
