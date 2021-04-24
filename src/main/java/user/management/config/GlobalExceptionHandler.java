package user.management.config;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import user.management.BaseController;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler extends BaseController {

    public GlobalExceptionHandler(ModelMapper modelMapper, Gson gson) {
        super(modelMapper, gson);
    }

}
