package com.verizonwireless.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.verizonwireless.exception.DataNotFoundException;
import com.verizonwireless.exception.ErrorMessage;
import com.verizonwireless.exception.InvalidDataException;

@ControllerAdvice
public class GlobalControllerAdvice {

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    ErrorMessage getErrorMessage(DataNotFoundException ex) {
        return new ErrorMessage(ex.getMessage());
    }
	
	@ExceptionHandler({InvalidDataException.class, EmptyResultDataAccessException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ErrorMessage getErrorMessage(Exception ex) {
        return new ErrorMessage(ex.getMessage());
    }

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ErrorMessage handleException(MethodArgumentNotValidException ex) {
	    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
	    List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
	    List<String> errors = new ArrayList<>(fieldErrors.size() + globalErrors.size());
	    String error;
	    for (FieldError fieldError : fieldErrors) {
	        error = fieldError.getField() + ", " + fieldError.getDefaultMessage();
	        errors.add(error);
	    }
	    for (ObjectError objectError : globalErrors) {
	        error = objectError.getObjectName() + ", " + objectError.getDefaultMessage();
	        errors.add(error);
	    }
	    return new ErrorMessage(errors);
	}

}