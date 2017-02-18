package co.anarquianegra.rockolappServidor;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import ListaOrdenada.IListaOrdenada;
import co.anarquianegra.rockolappServidor.mundo.Cancion;
import co.anarquianegra.rockolappServidor.mundo.ListaReproductor;
import co.anarquianegra.rockolappServidor.mundo.Servidor;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application implements ListChangeListener<Cancion> 
{
	//----------------------------
	//Constantes
	//----------------------------
	
	/**
	 * Representa el numero de puero por donde recibir conexiones
	 */
	public final static int PUERTO = 10052;
	
	//----------------------------
	//Atributos
	//----------------------------
	private ListaReproductor mundo;
	private ImageView rockola;
	private ImageView salir;
	private ImageView elegirCanciones;
	private ImageView reproducir;
	private ImageView pausar;
	private ImageView siguiente;
	private ImageView carpeta;
	private ImageView cancion;
	private ImageView qr;
	private Text reproduciendo;
	private Scene scene;
	private Stage stage;
	private Group root;
	private ArrayList<Text> textos;
	private boolean playing = false;
	private Servidor servidor;
	
	//----------------------------
	//Funciones
	//----------------------------
	
	private void inicializarRockola(double w, double h)
	{
		rockola = new ImageView(new Image(Main.class.getResourceAsStream("res/Jukebox.png"))); //$NON-NLS-1$
		rockola.setFitHeight(h-h/9);
		rockola.setTranslateY(h/25);
		rockola.setFitWidth(w);
		
		
		rockola.setOnMouseDragged(new EventHandler<MouseEvent>() {
			int i = 0;
			double dx = w;
			double dy = 0;
			double x = 0;
			double y = 0;
			boolean primero = true;
			
			public void handle(MouseEvent event) {
				dx = event.getX() - x;
				dy = event.getY() - y;
				
				if(Math.abs(dx) > Math.abs(dy) && !primero & i%5 == 0){
					if(dx > 0)
						stage.setX(stage.getX()+(w/15));
					else
						stage.setX(stage.getX()-(w/15));
				}
				
				primero = false;
				
				x = event.getX();
				y = event.getY();
					i++;
			};
		});
		
	}
	
	private void inicializarQR(double w, double h)
	{
		qr = new ImageView(new Image(Main.class.getResourceAsStream("res/QR.png"))); //$NON-NLS-1$
		qr.setFitWidth(h/14);
		qr.setFitHeight(h/14);
		qr.setX(w/2-h/28);
		qr.setY(h/14);
		
		// Manejar Eventos De Ratón
		
		qr.setOnMouseEntered(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.HAND);
			}
		});
		qr.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.DEFAULT);
			}
		});
		qr.setOnMouseClicked(new EventHandler<Event>() {
			boolean abierta = false;
			ImageView q ;
			@Override
			public void handle(Event event) {	
				if(!abierta)
				{
					//-------Boton QR-------
					qr.setFitWidth(h/18);
					qr.setFitHeight(h/18);
					//-----Imagen QR-----
					String ip = "";
					try {
						ip = Inet4Address.getLocalHost().getHostAddress();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String sec = "http://api.qrserver.com/v1/create-qr-code/?data="+ip+"&size="+(h/4)+"x"+(h/4)+"&margin=20";
					
					if(q != null)
						q.setImage(new Image(sec));
					else
					{
						q = new ImageView(new Image(sec));
						q.setFitWidth(h/4);
						q.setFitHeight(h/4);
						q.setX(w/2-h/8);
						q.setY(h/2-h/8);
					}
					root.getChildren().add(q);
				}
				else
				{
					root.getChildren().remove(q);
					//-------Boton QR-------
					qr.setFitWidth(h/16);
					qr.setFitHeight(h/16);
				}
				abierta = abierta == true ? false: true;

			}
		});
	}
	
	private void inicializarSalir(double w, double h)
	{
		salir = new ImageView(new Image(Main.class.getResourceAsStream("res/Salir.png"))); //$NON-NLS-1$
		salir.setFitWidth(h/20);
		salir.setFitHeight(h/20);
		salir.setX(w/2-rockola.getFitWidth()/4-h/20);
		salir.setY(h/6);
		
		// Manejar Eventos De Ratón
		
		salir.setOnMouseEntered(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.HAND);
			}
		});
		salir.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.DEFAULT);
			}
		});
		salir.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				try
				{
					mundo.siguiente(true);
					mundo.guardar();
					servidor.parar();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
	}
	
	private void inicializarReprodcir(double w, double h)
	{
		reproducir = new ImageView(new Image(Main.class.getResourceAsStream("res/Reproducir.png"))); //$NON-NLS-1$
		reproducir.setFitHeight(h/10);
		reproducir.setFitWidth(h/10);
		reproducir.setX(w/2-h/20);
		reproducir.setY(h/2+h*2/11);
		
		// Manejar Eventos De Ratón
		
		reproducir.setOnMouseEntered(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.HAND);
			}
		});
		reproducir.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.DEFAULT);
			}
		});
		reproducir.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event)
			{
				if(!playing)
				{
					mundo.reproducir();
					playing = true;
				}
			}
		});
	}
	
	private void inicializarPausar(double w, double h)
	{
		pausar = new ImageView(new Image(Main.class.getResourceAsStream("res/Pausar.png"))); //$NON-NLS-1$
		pausar.setFitHeight(h/12);
		pausar.setFitWidth(h/12);
		pausar.setX(w/2-rockola.getFitWidth()*2/11-h/24);
		pausar.setY(h/2+h*2/9);
		
		// Manejar Eventos De Ratón
		
		pausar.setOnMouseEntered(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.HAND);
			}
		});
		pausar.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.DEFAULT);
			}
		});
		pausar.setOnMouseClicked(new EventHandler<Event>() {
			boolean reproduciendo = false;
			@Override
			public void handle(Event event)
			{
				if(playing)
				{
					mundo.pausar();
					playing = false;
				}
			}
		});
	}
	
	private void inicializarSiguente(double w, double h)
	{
		siguiente = new ImageView(new Image(Main.class.getResourceAsStream("res/Siguiente.png"))); //$NON-NLS-1$
		siguiente.setFitHeight(h/12);
		siguiente.setFitWidth(h/12);
		siguiente.setX(w/2+rockola.getFitWidth()*2/11-h/24);
		siguiente.setY(h/2+h*2/9);
		
		// Manejar Eventos De Ratón
		
		siguiente.setOnMouseEntered(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.HAND);
			}
		});
		siguiente.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.DEFAULT);
			}
		});
		siguiente.setOnMouseClicked(new EventHandler<Event>() {
			boolean reproduciendo = false;
			@Override
			public void handle(Event event)
			{
				mundo.siguiente(false);
			}
		});
	}
	
	private void incicializarBuscar(double w, double h)
	{
		elegirCanciones = new ImageView(new Image(Main.class.getResourceAsStream("res/Buscar.png"))); //$NON-NLS-1$
		elegirCanciones.setFitWidth(h/20);
		elegirCanciones.setFitHeight(h/20);
		elegirCanciones.setX(w/2+rockola.getFitWidth()/4);
		elegirCanciones.setY(h/6);
		
		elegirCanciones.setOnMouseEntered(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.HAND);
			}
		});
		elegirCanciones.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.DEFAULT);
			}
		});
		elegirCanciones.setOnMouseClicked(new EventHandler<Event>() {
			boolean mostrando = false;
			@Override
			public void handle(Event event) {
				if(!mostrando)
				{
					//----Boton Bsucar---
					elegirCanciones.setFitWidth(h/22);
					elegirCanciones.setFitHeight(h/22);
					//-------------------
					String searchBoxCss = Main.class.getResource("res/SearchBox.css").toExternalForm();
					VBox vbox = new VBox();
					vbox.getStylesheets().add(searchBoxCss);
					vbox.setPrefWidth(50);
					vbox.setLayoutX(w/2+rockola.getFitWidth()/5);
					vbox.setLayoutY(h/8);
					vbox.setMaxWidth(Control.USE_PREF_SIZE);
					SearchBox sb = new SearchBox();
					sb.setOnKeyReleased(new EventHandler<KeyEvent>() {
						public void handle(KeyEvent event) {
							if(event.getCode().equals(KeyCode.ENTER))
							{
								String t = sb.darTextField().getText();
								System.out.println("Buscar: ... "+t);
								IListaOrdenada resulta = mundo.buscarCancionesPorNombre(t);
								IListaOrdenada resul = mundo.buscarCancionesPorArtista(t);
								int cant = resulta == null ? 0 : resulta.darLongitud();
								System.out.println("Cant : "+cant+" resulta "+resulta);
								cant += resul == null ? 0 : resul.darLongitud();
								System.out.println("Cant : "+cant);
								Cancion[] resultados = new Cancion[cant];
								int j = 0;
								for(int i = 0; resulta != null && i < resulta.darLongitud(); i++)
								{
									System.out.println("Cant adentro: "+cant+" resulta "+resulta);
									resultados[i] = (Cancion)resulta.dar(i);
									System.out.println("Cancion "+j+": "+((Cancion)resulta.dar(i)).toString());
									j = i;
								}
								for(int  i = 0; resul != null && i<resul.darLongitud(); i++)
								{
									if(j != 0)
										j++;
									System.out.println("i = "+i);
									resultados[j] = (Cancion)resul.dar(i);
									System.out.println("Cancion "+j+": "+((Cancion)resul.dar(i)).toString());
								}
								System.out.println("hay: "+resultados.length+" canciones");

								if(resultados.length > 0)
								{
									ChoiceDialog<Cancion> cd = new ChoiceDialog<Cancion>(resultados[0], Arrays.asList(resultados));
									Optional<Cancion> result = cd.showAndWait();
									if (result.isPresent()) {
										System.out.println("a reproducir : "+result.get());
										Stack<Cancion> cancion = new Stack<Cancion>();
										cancion.push(result.get());
										mundo.agregarLista(cancion, "");
									}
								}
							}
						};
					});
					
					vbox.getChildren().add(sb);
					root.getChildren().add(vbox);
					mostrando = true;
				}
				else
				{
					elegirCanciones.setFitWidth(h/20);
					elegirCanciones.setFitHeight(h/20);
					root.getChildren().remove(root.getChildren().size()-1);
					mostrando = false;
				}
			}
		});
	}
	
	private void incicializarCarpeta(double w, double h)
	{
		carpeta = new ImageView(new Image(Main.class.getResourceAsStream("res/Carpeta.png"))); //$NON-NLS-1$
		carpeta.setFitWidth(h/18);
		carpeta.setFitHeight(h/18);
		carpeta.setX(w/2-rockola.getFitWidth()/8-h/18);
		carpeta.setY(h/10);
		
		carpeta.setOnMouseEntered(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.HAND);
			}
		});
		carpeta.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.DEFAULT);
			}
		});
		carpeta.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				 carpeta.setFitWidth(h/20);
				 carpeta.setFitHeight(h/20);
				 DirectoryChooser chooser = new DirectoryChooser();
				 chooser.setTitle(Messages.getString("Cadena.ImportarCarpeta"));
				 File defaultDirectory = new File(System.getenv("HOME"));
				 chooser.setInitialDirectory(defaultDirectory);
				 File selectedDirectory = chooser.showDialog(stage);
				 carpeta.setFitWidth(h/18);
				 carpeta.setFitHeight(h/18);
				 if(selectedDirectory != null)
				 {
					 try
					 {
						 mundo.agregarCancion(selectedDirectory);
						 System.out.println("Dir: "+selectedDirectory);
					 } 
					 catch (IOException | SAXException | TikaException e) 
					 {
						 e.printStackTrace();
					 }
				 }
			}
		});
	}
	
	private void incicializarCancion(double w, double h)
	{
		cancion = new ImageView(new Image(Main.class.getResourceAsStream("res/Cancion.png"))); //$NON-NLS-1$
		cancion.setFitWidth(h/18);
		cancion.setFitHeight(h/18);
		cancion.setX(w/2+rockola.getFitWidth()/8);
		cancion.setY(h/10);
		
		cancion.setOnMouseEntered(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.HAND);
			}
		});
		cancion.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				scene.setCursor(Cursor.DEFAULT);
			}
		});
		cancion.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO Auto-generated method stub
				cancion.setFitWidth(h/20);
				cancion.setFitHeight(h/20);
				 FileChooser fileChooser = new FileChooser();
				 fileChooser.setTitle(Messages.getString("Cadena.ImportarCancion"));
				 fileChooser.getExtensionFilters().add(
				         new ExtensionFilter(Messages.getString("Cadena.tipoArchivo"), "*.wav", "*.mp3", "*.aac"));
				 List<File> selectedFile = fileChooser.showOpenMultipleDialog(stage);
				 cancion.setFitWidth(h/18);
				 cancion.setFitHeight(h/18);
				 if (selectedFile != null) {
					 for(File arch : selectedFile)
					 {
						 try {
							mundo.agregarCancion(arch);
						} catch (IOException | SAXException | TikaException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 }
				 }
			}
		});
	}
	
	private void inicializarTextos(double w, double h)
	{
		reproduciendo = new Text("#1"); //$NON-NLS-1$
		reproduciendo.setX(w/2-rockola.getFitWidth()/12);
		reproduciendo.setY(h*5/22);
		reproduciendo.setLineSpacing(h/80);
		reproduciendo.setWrappingWidth(rockola.getFitWidth()/5);
		reproduciendo.prefWidth(rockola.getFitWidth()/5);
		reproduciendo.maxWidth(Control.USE_PREF_SIZE);
		Font font = Font.font(12);
		reproduciendo.setFont(font);
		reproduciendo.setFill(Color.WHEAT);
		
		textos = new ArrayList<Text>();
		
		for(int i = 0;i<5; i++)
		{
			Text t = new Text("#"+(i+2));
			t.setLayoutX(w/2-rockola.getFitWidth()/15);
			t.setLayoutY(h*20/58+i*(h/20.4));
			t.setWrappingWidth(rockola.getFitWidth()/7);
			t.minHeight(h/60);
			t.prefHeight(h/60);
			t.maxHeight(h/40);
			Font f = Font.font(10);
			t.setFont(f);
			t.setFill(Color.WHEAT);
			textos.add(t);
		}
	}
	
	//----------------------------
	//Inicialización
	//----------------------------
	
	@Override
	public void start(Stage primaryStage) {
		try {
			mundo = new ListaReproductor(this);
			mundo.cargar();
			servidor = new Servidor(PUERTO, mundo);
			servidor.start();

			primaryStage.initStyle(StageStyle.TRANSPARENT);
			primaryStage.setResizable(false);
			root = new Group();
			stage = primaryStage;
			double w = 700;
			double h = Screen.getPrimary().getBounds().getHeight();
			inicializarRockola(w, h);
			inicializarSalir(w, h);
			incicializarBuscar(w, h);
			inicializarReprodcir(w, h);
			inicializarPausar(w, h);
			inicializarSiguente(w, h);
			incicializarCarpeta(w, h);
			incicializarCancion(w, h);
			inicializarQR(w, h);
			inicializarTextos(w, h);
			
			root.getChildren().addAll(rockola, salir, elegirCanciones, reproducir, pausar, siguiente, carpeta, cancion, reproduciendo, qr);
			for(Text t : textos)
			{
				root.getChildren().add(t);
			}
			scene = new Scene(root,w,h);
			scene.setFill(null);	
			
			primaryStage.setScene(scene);
			primaryStage.show();
		} 
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(TikaException e)
		{
			e.printStackTrace();
		}
		catch(SAXException e)
		{
			e.printStackTrace();
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void onChanged(javafx.collections.ListChangeListener.Change<? extends Cancion> c) {
		if(c.getList().size() > 0)
		{
			String s = c.getList().get(0).toString();
			reproduciendo.setText(s.substring(0, s.length() > 43 ? 43 : s.length()));
			System.out.println("Cambio lista");
			for(int i = 0; i<5; i++)
			{ 
				if(i+1 < c.getList().size())
				{
					s = c.getList().get(i+1).darNombre();
					textos.get(i).setText(s.substring(0, s.length() > 20 ? 20: s.length()));
				}
				else
				{
					textos.get(i).setText("#"+(i+2));
				}
			}
		}
	}
}
