package org.example.formulaserivce.FormulasMenegers;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.json.JSONObject;
import org.scilab.forge.jlatexmath.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Service
public class FormulasMeneger {


    public Resource RenderFormula(String latexFormula) {

        try {
            // Генерация изображения LaTeX формулы
            BufferedImage formulaImage = renderLatexFormula(latexFormula);

            // Создание нового DOCX-файла
            XWPFDocument document = new XWPFDocument();

            // Вставка изображения в документ
            insertImageIntoDocx(document, formulaImage);

            // Сохранение документа
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                document.write(outputStream);
                return new ByteArrayResource(outputStream.toByteArray());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    public Resource renderFormulaToPdf(String latexFormula) {
        try {
            // Генерация изображения LaTeX формулы
            BufferedImage formulaImage = renderLatexFormula(latexFormula);

            // Создание нового PDF-документа
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                // Преобразование изображения в формат PDF
                PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, convertBufferedImageToByteArray(formulaImage), "formula");

                // Вставка изображения в PDF
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.drawImage(pdImage, 50, 700, pdImage.getWidth() / 2, pdImage.getHeight() / 2); // Координаты и масштаб
                }

                // Сохранение PDF в поток
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    document.save(outputStream);
                    return new ByteArrayResource(outputStream.toByteArray());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private byte[] convertBufferedImageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    // Метод для рендера LaTeX формулы в изображение
    private BufferedImage renderLatexFormula(String latex) {

        try {
            TeXFormula formula = new TeXFormula(latex);
            return (BufferedImage) formula.createBufferedImage(
                    TeXConstants.STYLE_DISPLAY, // Стиль формулы
                    30f,                        // Размер шрифта
                    Color.BLACK,                // Цвет текста
                    Color.WHITE                 // Цвет фона
            );
        }catch (ParseException e) {
            TeXFormula formula = new TeXFormula(latex);
            return (BufferedImage) formula.createBufferedImage(
                    TeXConstants.STYLE_DISPLAY, // Стиль формулы
                    30f,                        // Размер шрифта
                    Color.BLACK,                // Цвет текста
                    Color.WHITE                 // Цвет фона
            );
        }
    }

    // Метод для вставки изображения в DOCX
    private void insertImageIntoDocx(XWPFDocument document, BufferedImage image) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();

        // Добавление изображения в документ
        run.addPicture(
                inputStream,                       // Входной поток изображения
                PictureType.BMP,         // Тип изображения
                "LatexFormula.png",                // Имя файла
                Units.toEMU(200),                  // Ширина изображения
                Units.toEMU(100)                   // Высота изображения
        );
    }

}
