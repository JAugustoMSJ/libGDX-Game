package com.augustodev.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class Escape extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture[] kid;
	private Texture kidJump;
	private Texture kidSlide;
	private Texture[] obstaculosPedras;
	private Texture obstaculoCerca;
	private Texture obstaculoGradeFerro;
	private Texture obstaculoRandomico;
	private Texture obstaculo;
	private Texture bkgSky, bkgHouses1, bkgHouses2;
	private Texture bkgMuro1, bkgMuro2, bkgGrass1, bkgGrass2;
	private Texture gameOver;
	private Texture score;
	private Rectangle kidForma;
	private Rectangle kidSlideForma;
	private Rectangle obstaculoForma;
	private BitmapFont mensagem;
	private BitmapFont instrucoes;
	private BitmapFont fonte;
	private ShapeRenderer shape;

	private Viewport viewport;
	private OrthographicCamera camera;
	private float larguraDisp;
	private float alturaDisp;
	private final int VIRTUAL_WIDTH = 1024;
	private final int VIRTUAL_HEIGHT = 768;
	private float disp_width;
	private float disp_height;
	private float bkgWidthPosicaoInicial = 0;
	private float bkgInternoWidthPosicaoInicial = 0;
	private static final float KID_WIDTH_INICIAL = 30;
	private float kidHeightPosicaoInicial;
	private float obstaculoPosicaoInicialHorizontal;
	private Random randomico;

	private int level = 0;
	private int cont = 0;
	private int estadoDoJogo = 0;
	private float pulo = 12;
	private int checkPulo = 0;
	private float variacao;
	private int variacao2 = 0;
	private int pontuacao = 0;
	private int MOVIMENTA_OBSTACULOS = 10;
	private float MOVIMENTA_BKG_EXTERNO = 10;
	private float MOVIMENTA_BKG_INTERNO = 2;
	private static final int SLIDE_HEIGHT_VARIACAO = 55;

	@Override
	public void create() {
	    shape = new ShapeRenderer();
	    score = new Texture("text_score.png");
//		disp_width = Gdx.graphics.getWidth();
//		disp_height = Gdx.graphics.getHeight();
//		larguraDisp e alturaDisp abaixo usados para suprir limitação nos eventos de pulo e slide
		larguraDisp = Gdx.graphics.getWidth();
		alturaDisp = Gdx.graphics.getHeight();
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		disp_width = VIRTUAL_WIDTH;
		disp_height = VIRTUAL_HEIGHT;
		mensagem = new BitmapFont();
		mensagem.getData().setScale(4);
		mensagem.setColor(Color.WHITE);
		instrucoes = new BitmapFont();
		instrucoes.getData().setScale(3);
		instrucoes.setColor(Color.WHITE);
		fonte = new BitmapFont();
		fonte.getData().setScale(5);
		fonte.setColor(Color.RED);
		kidForma = new Rectangle();
		kidSlideForma = new Rectangle();
		obstaculoForma = new Rectangle();
		batch = new SpriteBatch();
		bkgSky = new Texture("sky.png");
		bkgHouses1 = new Texture("houses.png");
		bkgHouses2 = new Texture("houses.png");
        bkgMuro1 = new Texture("muro.png");
        bkgMuro2 = new Texture("muro.png");
        bkgGrass1 = new Texture("grass.png");
        bkgGrass2 = new Texture("grass.png");
		gameOver = new Texture("gameover.png");
		kidSlide = new Texture("character_femalePerson_slide.png");
		kidJump = new Texture("character_femalePerson_jump.png");
		kid = new Texture[3];
		for (int i = 0; i < 3; i++)
			kid[i] = new Texture("character_femalePerson_run" + i + ".png");
		obstaculo = new Texture("pedra_001.png");
		obstaculosPedras = new Texture[3];
		for (int i = 0; i < 3; i++)
			obstaculosPedras[i] = new Texture("pedra_00" + (i+1) + ".png");
		obstaculoCerca = new Texture("fence.png");
		obstaculoGradeFerro = new Texture("fenceIron.png");
		obstaculoPosicaoInicialHorizontal = disp_width;
		kidHeightPosicaoInicial = disp_height / 6;
	}

	@Override
	public void render() {
		camera.update();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		variacao += Gdx.graphics.getDeltaTime() * 5;
		if (variacao > 2)
			variacao = 0;

		if (estadoDoJogo == 0 || estadoDoJogo == 2) {
			if (Gdx.input.justTouched()) {
				estadoDoJogo = 1;
			}
		}
//		--------------------- Inicia operações de jogo no estado 1 -----------------------------
		else if (estadoDoJogo == 1) {
			bkgWidthPosicaoInicial -= MOVIMENTA_BKG_EXTERNO;
			obstaculoPosicaoInicialHorizontal -= MOVIMENTA_OBSTACULOS;
			bkgInternoWidthPosicaoInicial -= MOVIMENTA_BKG_INTERNO;
			getLevel(pontuacao);
//		Se a posição atual for inferior ou igual a original ele mantém a posição original
//		checkPulo recebe 0 garantindo o retorno do Texture principal
			if ((kidHeightPosicaoInicial <= disp_height / 6 && checkPulo !=3 ) || checkPulo == 0) {
				kidHeightPosicaoInicial = disp_height / 6;
				checkPulo = 0;
			}
//		Caso 1 executa o pulo
			if (Gdx.input.justTouched() && Gdx.input.getX() <= larguraDisp/2
                    && kidHeightPosicaoInicial == disp_height / 6)
				checkPulo = 1;
			if (checkPulo == 1)
				kidHeightPosicaoInicial += pulo;
			if (checkPulo == 2)
				kidHeightPosicaoInicial -= pulo;
//		Condição de parada da subida no pulo
			if (kidHeightPosicaoInicial >= disp_height / 2 + 100)
				checkPulo = 2;
//		Condição de fim do slide
			if(checkPulo == 3 && Gdx.input.justTouched())
                checkPulo = 0;

//		Caso 3 executa o slide
			if(Gdx.input.justTouched() && Gdx.input.getX() > larguraDisp/2
					&& kidHeightPosicaoInicial <= disp_height/6){
				kidHeightPosicaoInicial = disp_height/6 - Escape.SLIDE_HEIGHT_VARIACAO;
				checkPulo = 3;
			}
//		Gera o loop dos obstáculos e atualiza a pontuação
			if (obstaculoPosicaoInicialHorizontal +
                    getObstaculoGradeFerroForma().getWidth() <= KID_WIDTH_INICIAL) {
				obstaculoPosicaoInicialHorizontal = disp_width;
				pontuacao ++;
				variacao2++;
				obstaculo = getObstaculoRandomico();
				    if(checkPulo == 3)
				    	checkPulo = 0;
			}

		}
//		------------------------- Encerra operações de jogo no estado 1 --------------------------
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bkgSky, 0, 0, disp_width, disp_height);
		batch.draw(bkgHouses1, bkgInternoWidthPosicaoInicial, 0, disp_width, disp_height);
		batch.draw(bkgHouses2, bkgInternoWidthPosicaoInicial + disp_width, 0, disp_width, disp_height);
		batch.draw(bkgMuro1, bkgWidthPosicaoInicial, 0, disp_width, disp_height);
        batch.draw(bkgMuro2, bkgWidthPosicaoInicial + disp_width, 0, disp_width, disp_height);
		batch.draw(obstaculo, obstaculoPosicaoInicialHorizontal, disp_height / 6);
		if(checkPulo == 3)
			batch.draw(kidSlide, KID_WIDTH_INICIAL, kidHeightPosicaoInicial);
		else if(checkPulo == 1 || checkPulo == 2)
			batch.draw(kidJump, KID_WIDTH_INICIAL, kidHeightPosicaoInicial);
		else
			batch.draw(kid[(int) variacao], KID_WIDTH_INICIAL, kidHeightPosicaoInicial);
		batch.draw(bkgGrass1, bkgWidthPosicaoInicial, 0, disp_width, disp_height);
        batch.draw(bkgGrass2, bkgWidthPosicaoInicial + disp_width, 0, disp_width, disp_height);
		fonte.draw(batch, String.valueOf(pontuacao), disp_width/2 + 100, disp_height - 40);
		batch.draw(score, disp_width/2 - 110, disp_height - score.getHeight() - 40);
//      Faz o loop do bkgMuro
		if(bkgWidthPosicaoInicial + disp_width <= 0)
            bkgWidthPosicaoInicial = 0;
//       Faz o loop do bkgInterno (Grama e Casas)
		if(bkgInternoWidthPosicaoInicial + disp_width <= 0)
			bkgInternoWidthPosicaoInicial = 0;

//		getShapeRenderer(obstaculo);

		if (estadoDoJogo == 0 || estadoDoJogo == 2) {
			mensagem.draw(batch, "Toque na tela\n  para iniciar!",
					disp_width / 2 - 150, disp_height / 2 - 50);
			instrucoes.draw(batch, String.valueOf("Toque a esquerda da tela para pular\n" +
					"Toque a direita da tela para deslizar"), disp_width / 2 - 300, disp_height / 5);
		}
		if (Intersector.overlaps(getKidForma(checkPulo), getObstaculoForma(obstaculo))) {
			batch.draw(gameOver, disp_width / 2 - 150, disp_height / 2);
			estadoDoJogo = 2;
			if (Gdx.input.justTouched()) {
				estadoDoJogo = 1;
				gameRestart();
			}
		}
		batch.end();

	}
//	---------------------------- Inicia a definição das formas ----------------------------------
	public Rectangle getKidForma(int checkPulo) {
		if(checkPulo != 3)
		kidForma.set(KID_WIDTH_INICIAL + 20, kidHeightPosicaoInicial,
				kid[0].getWidth() - 40, kid[0].getHeight() - 20);
//		Caso slide esteja ativado
		else {
			kidSlideForma.set(KID_WIDTH_INICIAL - 20, kidHeightPosicaoInicial,
					kidSlide.getWidth(), kidSlide.getHeight() - Escape.SLIDE_HEIGHT_VARIACAO);
			kidForma = kidSlideForma;
		}
		return kidForma;
	}

	public Rectangle getObstaculoPedraForma() {
		obstaculoForma.set(obstaculoPosicaoInicialHorizontal + 40, disp_height / 6,
				obstaculosPedras[0].getWidth() - 65, obstaculosPedras[0].getHeight() - 20);
		return obstaculoForma;
	}

	public Rectangle getObstaculoCercaForma() {
		obstaculoForma.set(obstaculoPosicaoInicialHorizontal + 20, disp_height / 6,
				obstaculoCerca.getWidth() - 50, obstaculoCerca.getHeight() - 50);
		return obstaculoForma;
	}

	public Rectangle getObstaculoGradeFerroForma() {
		obstaculoForma.set(obstaculoPosicaoInicialHorizontal, disp_height / 6 + 150,
				obstaculoGradeFerro.getWidth() - 20, obstaculoGradeFerro.getHeight() - 150);
		return obstaculoForma;
	}
//	Define padrão de forma de obstáculos a partir do obstáculo escolhido pelo getObstaculoRandomico
	public Rectangle getObstaculoForma(Texture obstaculo) {
		if(obstaculo.toString().equals("pedra_001.png")
				|| obstaculo.toString().equals("pedra_002.png")
				|| obstaculo.toString().equals("pedra_003.png"))
			obstaculoForma = getObstaculoPedraForma();

		else if(obstaculo.toString().equals("fence.png"))
			obstaculoForma = getObstaculoCercaForma();

		else
			obstaculoForma = getObstaculoGradeFerroForma();

		return obstaculoForma;
	}
//	---------------------------- Encerra a definição das formas ----------------------------------

//	public void getShapeRenderer(Texture obstaculo){
//		shape.begin(ShapeRenderer.ShapeType.Filled);
//		shape.rect(obstaculoForma.x, obstaculoForma.y,
//				getObstaculoForma(obstaculo).getWidth(),
//				getObstaculoForma(obstaculo).getHeight());
//
//
//		shape.rect(kidForma.x, kidForma.y,
//				getKidForma(checkPulo).getWidth(),
//				getKidForma(checkPulo).getHeight());
//		shape.end();
//	}
//	Chamado após Game Over
	public void gameRestart() {
		cont = 0;
		pontuacao = 0;
		kidHeightPosicaoInicial = disp_height/6;
		checkPulo = 0;
		pulo = 12;
		variacao = 0;
		obstaculoPosicaoInicialHorizontal = disp_width;
		obstaculo = getObstaculoRandomico();
		MOVIMENTA_BKG_EXTERNO = 10;
		MOVIMENTA_BKG_INTERNO = 2;
		MOVIMENTA_OBSTACULOS = 10;
	}

//	Define um obstáculo randômico
	public Texture getObstaculoRandomico() {
		int escolha;
		randomico = new Random();
		escolha = randomico.nextInt(3);
		switch (escolha) {
			case 0:
				if (randomico.nextInt(3) == 0)
					obstaculoRandomico = obstaculosPedras[0];
				else if (randomico.nextInt(3) == 1)
					obstaculoRandomico = obstaculosPedras[1];
				else
					obstaculoRandomico = obstaculosPedras[2];
				break;

			case 1:
				obstaculoRandomico = obstaculoCerca;
				break;

			case 2:
				obstaculoRandomico = obstaculoGradeFerro;
				break;
			default:
				obstaculoRandomico = obstaculo;
		}
		return obstaculoRandomico;
	}
//	Controla nível do jogo
	public void getLevel(int pontuacao){
			if (pontuacao >= 5 && pontuacao < 10){
				MOVIMENTA_BKG_EXTERNO = 12;
				MOVIMENTA_BKG_INTERNO = 2.5f;
				MOVIMENTA_OBSTACULOS = 12;
				variacao += Gdx.graphics.getDeltaTime() * 2;
				pulo = 14;
			}else if(pontuacao >= 10 && pontuacao < 15) {
				MOVIMENTA_BKG_EXTERNO = 14;
				MOVIMENTA_BKG_INTERNO = 3;
				MOVIMENTA_OBSTACULOS = 14;
				variacao += Gdx.graphics.getDeltaTime() * 3;
				pulo = 16;
			}else if(pontuacao >= 15 && pontuacao < 20) {
				MOVIMENTA_BKG_EXTERNO = 16;
				MOVIMENTA_BKG_INTERNO = 4;
				MOVIMENTA_OBSTACULOS = 16;
				variacao += Gdx.graphics.getDeltaTime() * 4;
				pulo = 18;
			}else if(pontuacao >= 20){
				MOVIMENTA_BKG_EXTERNO = 18;
				MOVIMENTA_BKG_INTERNO = 5;
				MOVIMENTA_OBSTACULOS = 18;
				variacao += Gdx.graphics.getDeltaTime() * 5;
				pulo = 20;
			}else{
					MOVIMENTA_BKG_INTERNO = 2;
					MOVIMENTA_BKG_EXTERNO = 10;
					MOVIMENTA_OBSTACULOS = 10;
				}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
