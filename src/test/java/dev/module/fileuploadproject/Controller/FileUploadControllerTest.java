package dev.module.fileuploadproject.Controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(FileUploadController.class)
public class FileUploadControllerTest {
    //
    @Autowired
    MockMvc mockMvc;

    @Test
    public void hello() throws Exception {
        /*
         *  request: /hello
         *  response:
         *      veiw: hello.html
         *      model:
         *          name: sample
         */
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(view().name("hello.html"))
                .andExpect(model().attribute("name", "sample"))
                .andExpect(content().string(containsString("")));
    }
}