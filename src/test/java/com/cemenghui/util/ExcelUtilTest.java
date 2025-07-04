package com.system.util;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ExcelUtilTest {

    @Test
    void testExportExcel_noException() throws Exception {
        ExcelUtil util = new ExcelUtil();
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ServletOutputStream sos = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {

            }

            @Override
            public void write(int b) throws IOException {}
        };
        Mockito.when(response.getOutputStream()).thenReturn(sos);

        util.exportExcel(
                Arrays.asList("A", "B"),
                Collections.singletonList(Arrays.asList("1", "2")),
                "testfile",
                response
        );

        Mockito.verify(response).setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        Mockito.verify(response).setHeader(Mockito.eq("Content-Disposition"), Mockito.contains("testfile.xlsx"));
        Mockito.verify(response).getOutputStream();
        // 不抛异常即为通过
    }
}