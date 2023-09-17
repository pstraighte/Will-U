package com.beteam.willu.common.exception;

import com.beteam.willu.common.ApiResponseDto;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.RejectedExecutionException;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {
    //기본
    @ExceptionHandler({IllegalArgumentException.class, DuplicateRequestException.class})
    public ResponseEntity<ApiResponseDto> handleException(Exception ex) {
        ApiResponseDto apiResponseDto = new ApiResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                apiResponseDto,
                HttpStatus.BAD_REQUEST
        );
    }

    //null값 에러
    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<ApiResponseDto> nullPointerExceptionHandler(NullPointerException ex) {
        ApiResponseDto apiResponseDto = new ApiResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                apiResponseDto,
                HttpStatus.BAD_REQUEST
        );
    }

    //유효값 아닐때
    @ExceptionHandler({NotValidInputException.class})
    public ResponseEntity<ApiResponseDto> notValidInputExceptionHandler(NotValidInputException ex) {
        ApiResponseDto apiResponseDto = new ApiResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                apiResponseDto,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({EntityNotFoundException.class}) //엔티티해서 데이터를 찾을수 없을떄->유저, 포스트 사용
    public ResponseEntity<ApiResponseDto> EntityNotFoundExceptionHandler(EntityNotFoundException ex) {
        ApiResponseDto apiResponseDto = new ApiResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                apiResponseDto,
                HttpStatus.BAD_REQUEST
        );
    }

    //사용자 접근 권한 필요 에러
    @ExceptionHandler({RejectedExecutionException.class})
    public ResponseEntity<ApiResponseDto> RejectedExecutionExceptionnHandler(RejectedExecutionException ex) {
        ApiResponseDto apiResponseDto = new ApiResponseDto(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                apiResponseDto,
                HttpStatus.BAD_REQUEST
        );
    }
}