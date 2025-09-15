//El cuerpo es una lista de segmentos (posiciones) que siguen a la cabeza y acaban en una cola.

//Cada vez que la serpiente se mueve, el cuerpo se “desplaza” para seguir la trayectoria.

//Cuando comes comida, agregamos un nuevo segmento (de 3 casillas) para que crezca.

//Cada vez que la cabeza se mueve, verificamos si su nueva posición coincide con algún segmento del cuerpo (excepto la cabeza misma).

//Si hay colisión, terminamos el juego o mostramos mensaje de “Game Over”.


package com.tujuego.serpiente;


import com.badlogic.gdx.ApplicationAdapter;
//Es una clase base proporcionada por LibGDX.
//Es la clase que necesitamos para crear un juego. Tiene métodos como create(), render(), dispose(), etc.

import com.badlogic.gdx.Gdx;
//Es una clase central de LibGDX con muchos métodos y propiedades estáticas.
//Te da acceso al sistema, entrada del usuario, archivos, audio, gráficos, etc

import com.badlogic.gdx.Input;
//Una interfaz que contiene constantes y métodos relacionados con la entrada (input).
//Permite trabajar con el teclado, ratón, pantalla táctil, etc.

import com.badlogic.gdx.graphics.GL20;
//Esta clase contiene constantes y métodos que el sistema entiende para hablar con la tarjeta gráfica.
//Por ejemplo, comandos para decir "borra toda la pantalla", "dibuja una línea", etc. Es como cuando borras una pizarra cuando vas a escribir algo nuevo.

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
//Es una herramienta para dibujar figuras básicas como líneas, círculos, rectángulos, etc.

import com.badlogic.gdx.graphics.g2d.BitmapFont;
//Es una herramienta de LibGDX que te permite escribir texto en la pantalla del juego.
//Imaginando que tenemos letras dibujadas como imágenes, BitmapFont las coge y las pinta donde tú le digas. Así puedes mostrar cosas como: "Puntaje: 100", "Game Over"

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//Es una herramienta para dibujar imágenes en pantalla en juegos hechos con LibGDX.
//si tenemos personajes, fondos, objetos, etc como imagenes, esto te permite pintar esas imagenes en la pantalla y puede juntar varios dibujos en unos solo para que funcione mejor.

import com.badlogic.gdx.files.FileHandle;
//Sirve para acceder a archivos que son internos, externos o de la red.

import com.badlogic.gdx.graphics.Texture;
//Sirve para cargar una imagen desde disco, es fundamental para mostrar gráficos, sprites o fondos en un juego.

import java.util.LinkedList;
//Es una clase de la biblioteca estándar de Java.
//Es una estructura de datos tipo lista enlazada. Útil para colas, pilas o listas donde se hacen muchas inserciones o eliminaciones.

import java.util.Random;
//Otra clase de la biblioteca estándar de Java. Sirve para generar números aleatorios

import com.badlogic.gdx.Preferences;
//Sirve para guardar y recuperar datos persistentes, como una base de datos local. Nos sirve para guardar el record de juego.

import com.badlogic.gdx.graphics.Color;
//Sirve para representar colores y se usa para cambiar el color de un objeto en pantalla, dibujar formas, etc.





public class MainGame extends ApplicationAdapter {
	private Texture manzanaTexture;
	private Texture gameOverTexture;
	private Texture cabezaTexture;
	private Texture cuerpoTexture;
	private Texture colaTexture;
	private Texture manzanaArcoirisTexture;

	
	int offsetY = 100; // esto es el espacio superior para el HUD 
	
 
	private void dibujarSerpiente(SpriteBatch batch) {
	    for (int i = 0; i < snakeBody.size(); i++) {
	        int[] segment = snakeBody.get(i);

	        float rotation = 0f;

	        if (i == 0) {
	            // Cabeza de la serpiente
	            switch (dir) {
	                case UP: rotation = 90f; break;
	                case DOWN: rotation = 270f; break;
	                case LEFT: rotation = 180f; break;
	                case RIGHT: rotation = 0f; break;
	            }

	            batch.draw(
	                cabezaTexture,
	                segment[0] * cellSize,
	                segment[1] * cellSize,
	                cellSize / 2f,
	                cellSize / 2f,
	                cellSize,
	                cellSize,
	                1f,
	                1f,
	                rotation,
	                0,
	                0,
	                cabezaTexture.getWidth(),
	                cabezaTexture.getHeight(),
	                false,
	                false
	            );

	        } else if (i == snakeBody.size() - 1) {
	            // Cola de la serpiente
	            int[] penultimo = snakeBody.get(snakeBody.size() - 2);
	            int[] ultimo = snakeBody.get(snakeBody.size() - 1);

	            if (ultimo[0] > penultimo[0]) {
	                rotation = 0f;
	            } else if (ultimo[0] < penultimo[0]) {
	                rotation = 180f;
	            } else if (ultimo[1] > penultimo[1]) {
	                rotation = 90f;
	            } else if (ultimo[1] < penultimo[1]) {
	                rotation = 270f;
	            }

	            batch.draw(
	                colaTexture,
	                ultimo[0] * cellSize,
	                ultimo[1] * cellSize,
	                cellSize / 2f,
	                cellSize / 2f,
	                cellSize,
	                cellSize,
	                1f,
	                1f,
	                rotation,
	                0,
	                0,
	                colaTexture.getWidth(),
	                colaTexture.getHeight(),
	                false,
	                false
	            );

	        } else {
	            // Segmentos del cuerpo de la serpiente
	            int[] prevSegment = snakeBody.get(i - 1);
	            int[] nextSegment = snakeBody.get(i + 1);

	            int dx = nextSegment[0] - prevSegment[0];
	            int dy = nextSegment[1] - prevSegment[1];

	            if (dx == 0 && dy != 0) {
	                rotation = 90f;
	            }

	            batch.draw(
	                cuerpoTexture,
	                segment[0] * cellSize,
	                segment[1] * cellSize,
	                cellSize / 2f,
	                cellSize / 2f,
	                cellSize,
	                cellSize,
	                1f,
	                1f,
	                rotation,
	                0,
	                0,
	                cuerpoTexture.getWidth(),
	                cuerpoTexture.getHeight(),
	                false,
	                false
	            );
	        }
	    }
	}

	
	ShapeRenderer shapeRenderer; // Lo usamos para dibujar figuras básicas (como cuadrados para la serpiente y comida)
	
	SpriteBatch batch; // Nos dibuja el texto en pantalla, como el puntaje o mensajes.
	
	BitmapFont font; // Nos muestra el texto, como "Gave Over".

	Texture appleTexture;

	public int cellSize = 30; // Cada celda del juego mide 30px (la cabeza de la serpiente ocupará 1 celda)
	final int gridWidth = 20;
	final int gridHeight = 20;
	// El tablero tiene 20 celdas de ancho x 20 de alto

	
	int x, y; // Guarda la posición (columna y fila) de la cabeza de la serpiente en el tablero
	
	int foodX, foodY; // Guarda la posición de la comida en el tablero

	int score = 0; // Son los puntos que ganas al comer, empieza en 0
	
	int highScore = 0; //Esto guardará la puntuación máxima obtenida en el juego, empieza en 0
	
	int lives = 3; // Son las vidas con las que empieza el juegador. Cada vez que la serpiente choca con su propio cuerpo, pierde una vida. Al llegar a 0, "Game Over"
	
	int level = 1; // Es el nivel actual del juego. Cada vez que cojas ciertos puntos, sube de nivel.
	
	boolean manzanaArcoirisActive = false;
	
	
	//VARIABLES PARA QUE PARPADEE EL RECORD CUANDO SE SUPERA:
	float blinkTimer = 0f;
	boolean showBlink = true;
		
		
	float elapsedTime = 0f; // Tiempo total jugado. 0f es un decimal (float), la f indica que es un float y no un double. 
							//Es float y no int porque guardará el tiempo en decimales.

	enum Direction {
		UP, DOWN, LEFT, RIGHT
	} // Posibles direcciones de movimiento

	Direction dir = Direction.RIGHT; // La dirección en la que empezará la serpiente será hacia la derecha

	float timer = 0;
	 float moveInterval = 0.15f; // esta es la velocidad inicial a la que se moverá la serpiente, que se reducirá con cada nivel.
	

	LinkedList<int[]> snakeBody; // Aquí se guardan las coordenadas del cuerpo de la serpiente
	
	int growSegments = 0; // Cuando la serpiente coma sumará 3 celdas. Aquí ponemos 0 porque es con lo que empieza, aun no ha comido
	
	boolean gameOver = false; // Te indica si el juego temrinó. Si es "true" ya no se mueve más ni acepta controles.

	Random random; // Esto generará posiciones aleatorias en la comida que vaya apareciendo

	
	float foodTimer = 0f;          // tiempo que lleva la fruta en pantalla
	float foodDuration = 5f;       // tiempo máximo antes de que desaparezca o se reinicie
	
	
	
	
	
	
	
	@Override
	// Este método está sobreescribiendo un método que viene de la clase padre de una interfaz.
	// En este caso, reemplaza el método create() de la clase ApplicationAdapter.


	public void create() {
		shapeRenderer = new ShapeRenderer(); // crea el objeto para dibujar formas (la serpiente y la comida)
		batch = new SpriteBatch(); // Crea el objeto para dibujar imágenes y texto.

		FileHandle assetsFolder = Gdx.files.internal("");
		System.out.println("Assets folder path: " + assetsFolder.path());

		
		FileHandle manzana = Gdx.files.internal("manzanaRoja.png");
		System.out.println("Existe manzanaRoja.png: " + manzana.exists());
		// Ahora carga la textura (solo si existe)
				if (manzana.exists()) {
					manzanaTexture = new Texture(manzana);
				} else {
					System.out.println("No se puede cargar la textura porque no existe manzana.png");
				}
	
				
		FileHandle manzanaArcoirisHandle = Gdx.files.internal("manzanaArcoiris.png");
				if (manzanaArcoirisHandle.exists()) {
				    manzanaArcoirisTexture = new Texture(manzanaArcoirisHandle);
				} else {
				    System.out.println("No se encontró la imagen de la manzana arcoiris.");
				}

				
		//Aquí pongo la imagen de la cabeza de serpiente
		FileHandle cabezaHandle = Gdx.files.internal("nuevaCabezaSerpiente.png");
				if (cabezaHandle.exists()) {
				    cabezaTexture = new Texture(cabezaHandle);
				} else {
				    System.out.println("No se encontró la imagen de la cabeza de la serpiente.");
				}
				
		FileHandle cuerpoHandle = Gdx.files.internal("nuevoCuerpoSerpiente.png");
		if (cuerpoHandle.exists()) cuerpoTexture = new Texture(cuerpoHandle);
		
		FileHandle colaHandle = Gdx.files.internal("nuevaColaSerpiente.png");
		if (colaHandle.exists()) colaTexture = new Texture(colaHandle);

	
		// Aquí pongo la carga de la imagen de Game Over que aparece cuando pierdes
		FileHandle gameOverHandle = Gdx.files.internal("gameOverMon.png");
		if (gameOverHandle.exists()) {
			gameOverTexture = new Texture(gameOverHandle);
		} else {
			System.out.println("No se encontró la imagen de Game Over.");
		}

		
	

		
		font = new BitmapFont(); // Crea la fuente para mostrar texto (puntajes, mensajes).
		// Cuando dibujas el HUD

		random = new Random(); // Crea el generador de números aleatorios para poner la comida en lugares distintos.

		x = gridWidth / 2;
		y = gridHeight / 2; // esto pone la cabeza de la serpiente justo en el centro del tablero.
		
		snakeBody = new LinkedList<>(); // Crea la lista que va a guardar todas las partes del cuerpo de la serpiente.
		snakeBody.add(new int[] { x, y }); // Añade la posición inicial de la cabeza (centro) a la lista del cuerpo.
		cellSize = 32; // Tamaño de cada celda del tablero 

		
		placeFood(); // Llama a un método que coloca la comida en una posición aleatoria.


		// Para cargar puntuación máxima desde preferencias
		Preferences prefs = Gdx.app.getPreferences("SnakeGamePrefs");
		highScore = prefs.getInteger("highScore", 0); // Si no existe, empezará en 0

	}

	
	
	
	
	private void placeFood() {
		while (true) { // Este bucle se va a repetir todo el rato
			foodX = random.nextInt(gridWidth);
			foodY = random.nextInt(gridHeight);

			boolean collision = false;
			for (int[] segment : snakeBody) {
				if (segment[0] == foodX && segment[1] == foodY) {
					collision = true;
					break;
				}
			}
			if (!collision)
				break;
		}
	

	manzanaArcoirisActive = random.nextInt(5) == 0; //Esto hará que a veces salga la manzana arcoris, Ej: 1 de cada 5 veces.
}
	
	
	
	
	@Override
	public void render() {
	    // Parpadeo para mostrar el texto "Record" intermitente
	    blinkTimer += Gdx.graphics.getDeltaTime();
	    if (blinkTimer >= 0.5f) {
	        showBlink = !showBlink;
	        blinkTimer = 0f;
	    }

	    
	    // El fondo del juego va a cambiar segun subas de nivel
	    float[] bgColor = getBackgroundColor(level);
	    Gdx.gl.glClearColor(bgColor[0], bgColor[1], bgColor[2], 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	    
	    // Dibujamos la cuadrícula 
	    drawGrid();

	    
	    // Actualización del juego si no ha terminado
	    if (!gameOver) {
	        elapsedTime += Gdx.graphics.getDeltaTime();
	        timer += Gdx.graphics.getDeltaTime();

	        if (timer >= moveInterval) {
	            timer = 0;

	            
	    // Actualizar la dirección segun el input
	     if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && dir != Direction.RIGHT) dir = Direction.LEFT;
	     		else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && dir != Direction.LEFT) dir = Direction.RIGHT;
	            else if (Gdx.input.isKeyPressed(Input.Keys.UP) && dir != Direction.DOWN) dir = Direction.UP;
	            else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && dir != Direction.UP) dir = Direction.DOWN;
	            
	            // Movimiento de la serpiente 
	            switch (dir) {
	                case LEFT: x -= 1; break;
	                case RIGHT: x += 1; break;
	                case UP: y += 1; break;
	                case DOWN: y -= 1; break;
	            }

	            
	            // Teletransporte por los bordes
	            if (x < 0) x = gridWidth - 1;
	            else if (x >= gridWidth) x = 0;
	            if (y < 0) y = gridHeight - 1;
	            else if (y >= gridHeight) y = 0;

	            
	            // Para añadir nueva cabeza al cuerpo
	            snakeBody.addFirst(new int[] { x, y });

	            if (growSegments > 0) {
	                growSegments--;
	            } else {
	                snakeBody.removeLast();
	            }

	            
	            // Comer fruta
	            if (x == foodX && y == foodY) {
	                growSegments += 3;

	                if (manzanaArcoirisActive) {
	                    score += 20;
	                } else {
	                    score += 10;
	                }


	                if (score > highScore) {
	                    highScore = score;
	                    Preferences prefs = Gdx.app.getPreferences("SnakeGamePrefs");
	                    prefs.putInteger("highScore", highScore);
	                    prefs.flush();
	                }

	                if (score % 50 == 0) {
	                	level++;
	                	moveInterval = Math.max(0.05f,  moveInterval - 0.01f);
	                }
	                placeFood();
	            }

	         // Actualizar temporizador de la fruta
	            foodTimer += Gdx.graphics.getDeltaTime();
	            if (foodTimer >= foodDuration) {
	                placeFood();   // Recoloca la fruta en otra posición
	                foodTimer = 0f; // Reinicia el temporizador
	            }
	            
	            // Colisión de la serpiente con su propio cuerpo
	            for (int i = 1; i < snakeBody.size(); i++) {
	                int[] segment = snakeBody.get(i);
	                if (segment[0] == x && segment[1] == y) {
	                    lives--;

	                    if (lives <= 0) {
	                        level = 1;
	                        score = 0;
	                        lives = 3;
	                        gameOver = true;
	                    } else {
	                        resetRound();
	                    }
	                    break;
	                }
	            }
	        }
	    }

	    
	
	    // DIBUJAR TODO
	    batch.begin();

	   
	    // Dibujar cuerpo de serpiente
	    for (int i = 0; i < snakeBody.size(); i++) {
	        int[] segment = snakeBody.get(i);

	        if (i == 0) {
	            
	        	// Cabeza
	            float rotation = 0f;
	            switch (dir) {
	                case UP:
	                	rotation = 90f;
	                	break;
	                case DOWN:
	                	rotation = 270f;
	                	break;
	                case LEFT:
	                	rotation = 180f;
	                	break;
	                case RIGHT:
	                	rotation = 0f;
	                	break;
	            };

	            batch.draw(cabezaTexture, segment[0] * cellSize, segment[1] * cellSize,
	                cellSize / 2f, cellSize / 2f,
	                cellSize, cellSize, 1f, 1f,
	                rotation, 0, 0,
	                cabezaTexture.getWidth(), cabezaTexture.getHeight(),
	                false, false);
	        } else if (i == snakeBody.size() - 1) {
	           
	        	// Cola
	            int[] prev = snakeBody.get(i - 1);
	            int dx = segment[0] - prev[0];
	            int dy = segment[1] - prev[1];

	            float rotation = (dx == 0 && dy != 0) ? 90f : 0f;

	            batch.draw(colaTexture, segment[0] * cellSize, segment[1] * cellSize,
	                cellSize / 2f, cellSize / 2f,
	                cellSize, cellSize, 1f, 1f,
	                rotation, 0, 0,
	                colaTexture.getWidth(), colaTexture.getHeight(),
	                false, false);
	        } else {
	            
	        	// Segmentos del cuerpo
	            int[] prev = snakeBody.get(i - 1);
	            int dx = segment[0] - prev[0];
	            int dy = segment[1] - prev[1];

	            float rotation = (dx == 0 && dy != 0) ? 90f : 0f;

	            batch.draw(cuerpoTexture, segment[0] * cellSize, segment[1] * cellSize,
	                cellSize / 2f, cellSize / 2f,
	                cellSize, cellSize, 1f, 1f,
	                rotation, 0, 0,
	                cuerpoTexture.getWidth(), cuerpoTexture.getHeight(),
	                false, false);
	        }
	    }

	    
	    // Dibujar manzana
	    if (manzanaArcoirisActive) {
	        batch.draw(manzanaArcoirisTexture, foodX * cellSize, foodY * cellSize, cellSize, cellSize);
	    } else {
	        batch.draw(manzanaTexture, foodX * cellSize, foodY * cellSize, cellSize, cellSize);
	    }


	    
	    // HUD de puntuación, vidas, nivel, tiempo
	    int textY = gridHeight * cellSize + 55;
	    font.draw(batch, "Puntos: " + score, 10, textY);
	    font.draw(batch, "Nivel: " + level, 150, textY);
	    font.draw(batch, "Vidas: " + lives, 250, textY);
	    font.draw(batch, "Tiempo: " + (int) elapsedTime + "s", 350, textY);

	    
	    // Record con efecto parpadeo
	    if (score >= highScore && showBlink) {
	        font.setColor(Color.GOLD);
	        font.draw(batch, "Record: " + highScore, 450, textY);
	        font.setColor(Color.WHITE);
	    } else {
	        font.setColor(Color.WHITE);
	        font.draw(batch, "Record: " + highScore, 450, textY);
	    }

	    
	    // Mensaje de Game Over
	    if (gameOver) {
	        if (gameOverTexture != null) {
	            float targetWidth = 400, targetHeight = 250;
	            float centerX = (Gdx.graphics.getWidth() - targetWidth) / 2f;
	            float centerY = (Gdx.graphics.getHeight() - targetHeight) / 2f;

	            batch.draw(gameOverTexture, centerX, centerY, targetWidth, targetHeight);
	            font.draw(batch, "Pulsa ESPACIO para reiniciar", centerX + 20, centerY - 20);
	        } else {
	            font.draw(batch, "GAME OVER! Pulsa ESPACIO para reiniciar.", 50, Gdx.graphics.getHeight() / 2);
	        }
	    }

	    batch.end();

	    
	    // Reinicio del juego si se pulsa espacio
	    if (gameOver && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
	        resetGame();
	    }
	}

	
	
	private void drawGrid() {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);

		int offsetY = 100; // mismo offset que ya he usado en el render
		
		for (int i = 0; i <= gridWidth; i++) {
			shapeRenderer.line(i * cellSize, 0, i * cellSize, gridHeight * cellSize);
		}

		for (int j = 0; j <= gridHeight; j++) {
			shapeRenderer.line(0, j * cellSize, gridWidth * cellSize, j * cellSize);
		}

		shapeRenderer.end();
	}
	
	
	
	private void resetRound() {
	    x = gridWidth / 2;
	    y = gridHeight / 2;
	    dir = Direction.RIGHT;
	    snakeBody.clear();
	    snakeBody.add(new int[] { x, y });
	    growSegments = 0;
	    moveInterval = 0.15f;
	    placeFood();
	    gameOver = false; //esto es para que el juego continue
	}

	
	

	private void resetGame() {
		x = gridWidth / 2;
		y = gridHeight / 2;
		dir = Direction.RIGHT;
		snakeBody.clear();
		snakeBody.add(new int[] { x, y });
		growSegments = 0;
		moveInterval = 0.15f;
		placeFood();
		gameOver = false;
	}

	@Override
	public void dispose() {
		shapeRenderer.dispose();
		batch.dispose();
		font.dispose();
		
		  if (manzanaTexture != null) manzanaTexture.dispose();
		    if (gameOverTexture != null) gameOverTexture.dispose();
		    if (cabezaTexture != null) cabezaTexture.dispose(); //  Aquí estoy liberando la imagen de la cabeza
		    if (manzanaArcoirisTexture != null) manzanaArcoirisTexture.dispose(); // 
		}
	
	
	float[][] backgroundColors = {
		    { 0f, 0f, 0f },
		    { 0.1f, 0.1f, 0.3f },
		    { 0.1f, 0.3f, 0.1f },
		    { 0.3f, 0.1f, 0.1f },
		    { 0.3f, 0.3f, 0f }
		};

		public float[] getBackgroundColor(int level) {
		    int index = (level - 1) % backgroundColors.length;
		    return backgroundColors[index];
		}
}


