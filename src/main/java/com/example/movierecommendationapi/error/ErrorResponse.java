package com.example.movierecommendationapi.error;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponse(int status, String message, String path){
        this.status = status;
        this.message = message;
        this.path = path;
        timestamp = LocalDateTime.now();
    }

    public int getStatus() {return status;}
    public String getMessage() {return message;}
    public LocalDateTime getTimestamp() {return timestamp;}

}
