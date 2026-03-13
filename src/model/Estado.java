package model;

public enum Estado {
    DISENO,      // Se está escribiendo (Draft)
    LISTO,       // Diseño terminado, listo para probar
    EJECUTANDO,  // El tester lo está probando ahora
    PASADO,      // Funciona como se esperaba
    FALLIDO,     // Se encontró un Bug
    BLOQUEADO,   // No se puede probar por un error ajeno
    OMITIDO      // No se probará en este ciclo de pruebas
}