package com.sqlgen;

import com.sqlgen.dto.SqlRequest;
import com.sqlgen.dto.SqlResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sql")
public class SqlController {

    private final SqlGeneratorService sqlGeneratorService;

    public SqlController(SqlGeneratorService sqlGeneratorService) {
        this.sqlGeneratorService = sqlGeneratorService;
    }

    @PostMapping("/gen")
    public ResponseEntity<SqlResponse> generateSql(@Valid @RequestBody SqlRequest request) {
        String sql = sqlGeneratorService.generate(request.getPrompt());
        return ResponseEntity.ok(new SqlResponse(sql));
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World";
    }
}
