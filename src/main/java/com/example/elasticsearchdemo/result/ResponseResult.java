package com.example.elasticsearchdemo.result;

import java.util.List;

/**
 * @Description: TODO 响应结果
 * @author: jlsong E-mail: rainmap@163.com
 * @date: created in 下午2:40 19-7-1
 */
public class ResponseResult {

    private String requestId;
    private int retCode;
    private String message;
    private String response;

    public ResponseResult(String requestId, int retCode, String message, String response) {
        this.requestId = requestId;
        this.retCode = retCode;
        this.message = message;
        this.response = response;
    }


    @Override
    public String toString() {
        return "ResponseResult{" +
                "requestId='" + requestId + '\'' +
                ", retCode=" + retCode +
                ", message='" + message + '\'' +
                ", response='" + response + '\'' +
                '}';
    }

    public static class Bucket{
        public String key;
        public int docCount;
        public List<Bucket> buckets;

        public Bucket(String key, int docCount, List<Bucket> buckets) {
            this.key = key;
            this.docCount = docCount;
            this.buckets = buckets;
        }
    }
}
