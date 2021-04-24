package user.management.response;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class GenericResponse<T> {

    @Expose
    T data;
    @Expose
    HttpStatus status;

    public GenericResponse() {

    }

    public GenericResponse(T data, HttpStatus status) {
        this.data = data;
        this.status = status;
    }
}
