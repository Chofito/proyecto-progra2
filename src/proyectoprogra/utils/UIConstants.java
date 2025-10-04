package proyectoprogra.utils;

import java.awt.Color;
import java.awt.Font;

public class UIConstants {
    
    public static final Color PRIMARY_COLOR = new Color(30, 136, 229);
    public static final Color SUCCESS_COLOR = new Color(40, 180, 99);
    public static final Color WARNING_COLOR = new Color(255, 152, 0);
    public static final Color DANGER_COLOR = new Color(244, 67, 54);
    public static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    public static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    public static final Color BORDER_COLOR = new Color(222, 226, 230);
    
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    public static final int BUTTON_HEIGHT = 40;
    public static final int BUTTON_WIDTH = 120;
    public static final int FIELD_HEIGHT = 45;
    public static final int FIELD_WIDTH = 250;
    public static final int PADDING = 15;
    public static final int SMALL_PADDING = 10;
    
    public static final int BORDER_RADIUS = 8;
    public static final int BORDER_THICKNESS = 1;
    
    public static final int FRAME_WIDTH = 1000;
    public static final int FRAME_HEIGHT = 700;
    public static final int MIN_FRAME_WIDTH = 800;
    public static final int MIN_FRAME_HEIGHT = 600;
    
    public static final String APP_TITLE = "Sistema de Gestión de Viajes - UMG";
    public static final String SUCCESS_MESSAGE = "Operación realizada exitosamente";
    public static final String ERROR_MESSAGE = "Ha ocurrido un error";
    public static final String VALIDATION_ERROR = "Por favor, complete todos los campos correctamente";
    public static final String DELETE_CONFIRMATION = "¿Está seguro de que desea eliminar este viaje?";
    
    public static final String ADD_BUTTON_TEXT = "Agregar";
    public static final String UPDATE_BUTTON_TEXT = "Actualizar";
    public static final String CLEAR_BUTTON_TEXT = "Limpiar";
    public static final String SEARCH_BUTTON_TEXT = "Buscar";
    public static final String DELETE_BUTTON_TEXT = "Eliminar";
    public static final String REFRESH_BUTTON_TEXT = "Refrescar";
    public static final String DOWNLOAD_JSON_BUTTON_TEXT = "Descargar JSON";
    public static final String DOWNLOAD_EXCEL_BUTTON_TEXT = "Descargar Excel";
    public static final String ACCEPT_BUTTON_TEXT = "Aceptar";
    public static final String CANCEL_BUTTON_TEXT = "Cancelar";
    
    public static final String ORIGIN_LABEL = "Origen:";
    public static final String DESTINATION_LABEL = "Destino:";
    public static final String DEPARTURE_DATE_LABEL = "Fecha Salida:";
    public static final String ARRIVAL_DATE_LABEL = "Fecha Llegada:";
    public static final String STATUS_LABEL = "Estado:";
    public static final String SEARCH_LABEL = "Buscar Viaje:";
    public static final String TRIPS_TABLE_LABEL = "Viajes Registrados";
    public static final String NEW_TRIP_FORM_TITLE = "Agregar Nuevo Viaje";
    public static final String SELECT_DATE_TITLE = "Seleccionar Fecha";
    public static final String SELECT_TIME_TITLE = "Seleccionar Hora";
    public static final String TIME_LABEL = "Hora:";
    public static final String APP_MAIN_TITLE = "Sistema de Gestión de Viajes";
    
    public static final String ORIGIN_PLACEHOLDER = "Ciudad de origen";
    public static final String DESTINATION_PLACEHOLDER = "Ciudad de destino";
    public static final String DEPARTURE_DATE_PLACEHOLDER = "dd/MM/yyyy HH:mm";
    public static final String ARRIVAL_DATE_PLACEHOLDER = "dd/MM/yyyy HH:mm";
    public static final String SEARCH_PLACEHOLDER = "Buscar por origen o destino...";
    
    public static final String REQUIRED_FIELD_ERROR = "Campo requerido";
    public static final String SUCCESS_ADD_TRIP = "Viaje agregado exitosamente";
    public static final String SUCCESS_UPDATE_TRIP = "Viaje actualizado exitosamente";
    public static final String SUCCESS_DELETE_TRIP = "Viaje eliminado exitosamente";
    public static final String ERROR_ADD_TRIP = "Error al agregar el viaje";
    public static final String ERROR_UPDATE_TRIP = "Error al actualizar el viaje";
    public static final String ERROR_DELETE_TRIP = "Error al eliminar el viaje";
    public static final String ERROR_PROCESS_DATE = "Error al procesar la fecha";
    public static final String ERROR_DEPARTURE_AFTER_ARRIVAL = "La fecha de salida no puede ser posterior a la fecha de llegada";
    public static final String ERROR_ARRIVAL_BEFORE_DEPARTURE = "La fecha de llegada no puede ser anterior a la fecha de salida";
    
    public static final String SUCCESS_TITLE = "Éxito";
    public static final String ERROR_TITLE = "Error";
    public static final String DELETE_CONFIRMATION_TITLE = "Confirmar eliminación";
    public static final String YES_OPTION = "Sí";
    public static final String NO_OPTION = "No";
    
    public static final String EDIT_TOOLTIP = "Editar viaje";
    public static final String DELETE_TOOLTIP = "Eliminar viaje";
    public static final String LOGO_ERROR_MESSAGE = "No se pudo cargar el logo UMG: ";
    public static final String CALENDAR_COMBOBOX_ERROR = "No se pudieron personalizar los ComboBoxes del calendario: ";
}
