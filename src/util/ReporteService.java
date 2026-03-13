package util;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import model.CasoPrueba;
import model.Escenario;
import model.Estado;

import java.awt.Color;
import java.io.FileOutputStream;
import java.util.List;

public class ReporteService {


    public void generarReporteEscenario(Escenario escenario, List<CasoPrueba> casos, String fechaHora, String ruta) {
        Document documento = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();

            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.GRAY);
            Font fuenteFecha = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.LIGHT_GRAY); // Fuente para la fecha
            Font fuenteTexto = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font fuenteGherkin = FontFactory.getFont(FontFactory.COURIER, 8, Color.DARK_GRAY);

           
            Paragraph titulo = new Paragraph("Reporte de Ejecución de QA", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

           
            Paragraph pFecha = new Paragraph("Generado el: " + fechaHora, fuenteFecha);
            pFecha.setAlignment(Element.ALIGN_CENTER);
            documento.add(pFecha);
            documento.add(new Paragraph(" ")); 

            documento.add(new Paragraph("Escenario: " + escenario.getNombre(), fuenteSubtitulo));
            documento.add(new Paragraph("Total de Casos: " + casos.size(), fuenteTexto));
            documento.add(new Paragraph(" ", fuenteTexto));

            PdfPTable tabla = new PdfPTable(3);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[] { 3f, 2f, 5f });

            agregarCeldaCabecera(tabla, "Caso de Prueba");
            agregarCeldaCabecera(tabla, "Estado");
            agregarCeldaCabecera(tabla, "Pasos (Gherkin)");

            for (CasoPrueba caso : casos) {
                tabla.addCell(new Phrase(caso.getNombre(), fuenteTexto));
                PdfPCell celdaEstado = new PdfPCell(new Phrase(caso.getEstado().toString(), fuenteTexto));
                celdaEstado.setHorizontalAlignment(Element.ALIGN_CENTER);
                configurarColorEstado(celdaEstado, caso.getEstado());
                tabla.addCell(celdaEstado);

                String pasos = "GIVEN: " + caso.getGivenStep() + "\nWHEN: " + caso.getWhenStep() + "\nTHEN: " + caso.getThenStep();
                tabla.addCell(new Phrase(pasos, fuenteGherkin));
            }

            documento.add(tabla);
            documento.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void generarReporteConMetricas(String escenario, List<CasoPrueba> casos, String fechaHora, String ruta) {
        Document documento = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();

            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(0, 51, 102));
            Font fuenteTexto = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font fuenteBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

            Paragraph titulo = new Paragraph("📊 DASHBOARD DE MÉTRICAS QA", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            
            Paragraph fecha = new Paragraph("Generado el: " + fechaHora, fuenteTexto);
            fecha.setAlignment(Element.ALIGN_CENTER);
            documento.add(fecha);
            documento.add(new Paragraph(" "));

            documento.add(new Paragraph("Escenario Analizado: " + escenario, fuenteBold));
            documento.add(new Paragraph(" ", fuenteTexto));

            int total = casos.size();
            long pasados = casos.stream().filter(c -> c.getEstado() == Estado.PASADO).count();
            long fallidos = casos.stream().filter(c -> c.getEstado() == Estado.FALLIDO).count();
            double efectividad = total > 0 ? (pasados * 100.0) / total : 0;

            PdfPTable tablaResumen = new PdfPTable(2);
            tablaResumen.setWidthPercentage(60);
            tablaResumen.setHorizontalAlignment(Element.ALIGN_LEFT);

            agregarCeldaSimple(tablaResumen, "Total de Casos de Prueba:", fuenteBold);
            agregarCeldaSimple(tablaResumen, String.valueOf(total), fuenteTexto);
            agregarCeldaSimple(tablaResumen, "Casos Pasados:", fuenteBold);
            agregarCeldaSimple(tablaResumen, String.valueOf(pasados), fuenteTexto);
            agregarCeldaSimple(tablaResumen, "Casos Fallidos:", fuenteBold);
            agregarCeldaSimple(tablaResumen, String.valueOf(fallidos), fuenteTexto);
            agregarCeldaSimple(tablaResumen, "Porcentaje de Éxito:", fuenteBold);
            agregarCeldaSimple(tablaResumen, String.format("%.2f%%", efectividad), fuenteTexto);

            documento.add(tablaResumen);
            documento.add(new Paragraph("\nDetalle de Ejecución:\n\n", fuenteBold));

            PdfPTable tablaDetalle = new PdfPTable(2);
            tablaDetalle.setWidthPercentage(100);
            tablaDetalle.setWidths(new float[] { 7f, 3f });

            agregarCeldaCabecera(tablaDetalle, "Nombre del Caso");
            agregarCeldaCabecera(tablaDetalle, "Estado Final");

            for (CasoPrueba caso : casos) {
                tablaDetalle.addCell(new Phrase(caso.getNombre(), fuenteTexto));
                PdfPCell celdaEstado = new PdfPCell(new Phrase(caso.getEstado().toString(), fuenteTexto));
                celdaEstado.setHorizontalAlignment(Element.ALIGN_CENTER);
                configurarColorEstado(celdaEstado, caso.getEstado());
                tablaDetalle.addCell(celdaEstado);
            }

            documento.add(tablaDetalle);
            documento.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //MÉTODOS AUXILIARES

    private void agregarCeldaCabecera(PdfPTable tabla, String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
        celda.setBackgroundColor(new Color(38, 139, 210)); 
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setPadding(5);
        tabla.addCell(celda);
    }

    private void agregarCeldaSimple(PdfPTable tabla, String texto, Font fuente) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setPadding(3);
        tabla.addCell(celda);
    }

    private void configurarColorEstado(PdfPCell celda, Estado estado) {
        switch (estado) {
            case PASADO -> celda.setBackgroundColor(new Color(210, 255, 210));
            case FALLIDO -> celda.setBackgroundColor(new Color(255, 210, 210));
            case BLOQUEADO -> celda.setBackgroundColor(new Color(240, 210, 255));
            case EJECUTANDO -> celda.setBackgroundColor(new Color(210, 230, 255));
            case LISTO -> celda.setBackgroundColor(new Color(255, 245, 200));
            default -> celda.setBackgroundColor(Color.WHITE);
        }
    }
}