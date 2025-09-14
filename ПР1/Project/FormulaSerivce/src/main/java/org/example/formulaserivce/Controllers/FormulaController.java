package org.example.formulaserivce.Controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.example.formulaserivce.Analizators.VisualizationCompare;
import org.example.formulaserivce.FormulasMenegers.FormulasMeneger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Tag(name = "Formalas Methods")
@RestController()
@RequestMapping("/api/formulas")
public class FormulaController {


    @Autowired
    private FormulasMeneger formulasMeneger;

    @Autowired
    private VisualizationCompare visualizationCompare;

    @PostMapping("/create")
    public ResponseEntity<Resource> create(@RequestBody String body) {
        JSONObject obj = new JSONObject(body);
        ResponseEntity<Resource> response;
        String latex;
        System.out.println(obj.toString(2));
        System.out.println(obj.get("latex") instanceof JSONArray);
        if(obj.get("latex") instanceof JSONArray) {
            latex = obj.getJSONArray("latex").getString(0);
        }else{
            latex = obj.getString("latex");
        }
        System.out.println(latex);
        Resource file= obj.getString("mode").equals("docx") ? formulasMeneger.RenderFormula(latex) :
                formulasMeneger.renderFormulaToPdf(latex);
        System.out.println(file);
        if(file==null){
          return ResponseEntity.notFound().build();
        }else{
            response = ResponseEntity.ok(file);
            return response;
        }
    }
    @PostMapping("/analyz")
    public ResponseEntity<String> analyze(@RequestHeader HttpHeaders headers, @RequestBody String body) {
        JSONObject obj = new JSONObject(body);
        System.out.println(obj.toString(2)+ "Analyz");
        ResponseEntity<String> response = null;
        String formula;
        if(obj.get("formula") instanceof JSONArray) {
            formula = obj.getJSONArray("formula").getString(0);
        }else{
            formula = obj.getString("formula");
        }
        String token = headers.getFirst("Authorization");
        String login = obj.getString("login");
        try {
            String sim= visualizationCompare.analyze(formula,login,token);
            response = ResponseEntity.ok(sim.toString());

        } catch (FileNotFoundException e) {
            response = ResponseEntity.badRequest().build();
        }
        return response;
    }


    private boolean isLatex(String formula){
        if(formula.contains("\\")|| formula.contains("{")||formula.contains("}")){
            return true;
        }else{
            return false;
        }
    }

    @PostMapping("/import")
    public ResponseEntity<String> importFile(@RequestParam("file") MultipartFile file) throws IOException, Docx4JException {
        System.out.println("test");
        if (file != null) {
            System.out.println("Файл получен: " + file.getOriginalFilename());
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file.getInputStream());

            // Получение всех параграфов из основного содержимого
            List<Object> paragraphs = wordMLPackage.getMainDocumentPart().getContent();

            // Итерация по параграфам
            for (Object obj : paragraphs) {
                String p = obj.toString();
                if(isLatex(p)){
                    p=p.replace("\\\\","\\");
                    JSONObject obj1 = new JSONObject().append("latex",p);
                    return ResponseEntity.ok(obj1.toString(2));
                }
            }
        }
        return ResponseEntity.badRequest().body("Файл не был загружен.");
    }

}
