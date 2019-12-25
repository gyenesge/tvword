package home.gabe.tvword.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ModelAndView handleException(HttpServletRequest request, Exception ex) {

        if (ex instanceof TVWordException) {
            log.error("Business error in {} with code {} and message: {}", request.getRequestURL(), ((TVWordException) ex).getErrorCode(), ex.getMessage());
        } else {
            log.error("Exception received.", ex);
        }

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/error/general");
        mav.addObject("exception", ex);
        mav.addObject("errorCode", ex instanceof TVWordException ? ((TVWordException) ex).getErrorCode() : TVWordException.EC_GENERAL_ERROR);
        mav.addObject("url", request.getRequestURL());

        return mav;
    }

}
