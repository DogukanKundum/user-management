package user.management;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import user.management.response.GenericResponse;

@Component
public class BaseController {

    protected Gson gson;
    protected ModelMapper modelMapper;

    public BaseController(ModelMapper modelMapper, Gson gson){
        this.gson =  gson;
        this.modelMapper = modelMapper;

    }

    public ResponseEntity success(GenericResponse response) {
        response.setStatus(HttpStatus.OK);
        String json = gson.toJson(response);
        return new ResponseEntity<String>(json, HttpStatus.OK);
    }

    public <T> ResponseEntity success(T data) {
        return new ResponseEntity<T>(data, HttpStatus.OK);
    }

    public ResponseEntity fail(GenericResponse response) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        String json = gson.toJson(response);
        return new ResponseEntity(json, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity fail(GenericResponse response, String message) {
        response.setData(message);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        String json = gson.toJson(response);
        return new ResponseEntity(json, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity fail(String message) {
        GenericResponse response = new GenericResponse();
        response.setData(message);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        String json = gson.toJson(response);
        return new ResponseEntity(json, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity notFound(GenericResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND);
        String json = gson.toJson(response);
        return new ResponseEntity(json, HttpStatus.NOT_FOUND);
    }

    public <T> ResponseEntity notFound(T data) {
        GenericResponse<T> response = new GenericResponse<T>();
        response.setData(data);
        response.setStatus(HttpStatus.OK);
        String json = gson.toJson(response);
        return new ResponseEntity<String>(json, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity found(GenericResponse response) {
        response.setStatus(HttpStatus.FOUND);
        String json = gson.toJson(response);
        return new ResponseEntity(json, HttpStatus.FOUND);
    }

    public ResponseEntity duplicate(GenericResponse response) {
        response.setStatus(HttpStatus.IM_USED);
        String json = gson.toJson(response);
        return new ResponseEntity(json, HttpStatus.IM_USED);
    }

}
