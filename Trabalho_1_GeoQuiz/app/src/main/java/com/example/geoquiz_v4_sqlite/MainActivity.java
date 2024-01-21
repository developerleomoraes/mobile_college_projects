package com.example.geoquiz_v4_sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*
  Modelo de projeto para a Atividade 1.
  Será preciso adicionar o cadastro das respostas do usuário ao Quiz, conforme
  definido no Canvas.

  GitHub: https://github.com/udofritzke/GeoQuiz
 */

public class MainActivity extends AppCompatActivity {
    private Button mBotaoVerdadeiro;
    private Button mBotaoFalso;
    private Button mBotaoProximo;

    // Botão removido conforme solicitado
    //private Button mBotaoCadastra;
    private Button mBotaoMostra;
    private Button mBotaoDeleta;

    private Button mBotaoCola;

    private TextView mTextViewQuestao;
    private TextView mTextViewQuestoesArmazenadas;

    private static final String TAG = "QuizActivity";
    private static final String CHAVE_INDICE = "INDICE";
    private static final int CODIGO_REQUISICAO_COLA = 0;

    private Questao[] mBancoDeQuestoes = new Questao[]{
            new Questao(R.string.questao_suez, true),
            new Questao(R.string.questao_alemanha, false)
    };

    QuestaoDB mQuestoesDb;

    private int mIndiceAtual = 0;

    private boolean mEhColador;

    @Override
    protected void onCreate(Bundle instanciaSalva) {
        super.onCreate(instanciaSalva);
        setContentView(R.layout.activity_main);
        //Log.d(TAG, "onCreate()");
        if (instanciaSalva != null) {
            mIndiceAtual = instanciaSalva.getInt(CHAVE_INDICE, 0);
        }

        mTextViewQuestao = (TextView) findViewById(R.id.view_texto_da_questao);
        atualizaQuestao();

        mBotaoVerdadeiro = (Button) findViewById(R.id.botao_verdadeiro);
        // utilização de classe anônima interna
        mBotaoVerdadeiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaResposta(true);
            }
        });

        mBotaoFalso = (Button) findViewById(R.id.botao_falso);
        mBotaoFalso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaResposta(false);
            }
        });

        // Botão 'PROXIMO'
        mBotaoProximo = (Button) findViewById(R.id.botao_proximo);
        mBotaoProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIndiceAtual = (mIndiceAtual + 1) % mBancoDeQuestoes.length;
                mEhColador = false;
                atualizaQuestao();
            }
        });

        // Botão 'COLA'
        mBotaoCola = (Button) findViewById(R.id.botao_cola);
        mBotaoCola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inicia ColaActivity
                // Intent intent = new Intent(MainActivity.this, ColaActivity.class);
                boolean respostaEVerdadeira = mBancoDeQuestoes[mIndiceAtual].isRespostaCorreta();
                Intent intent = ColaActivity.novoIntent(MainActivity.this, respostaEVerdadeira);
                //startActivity(intent);
                startActivityForResult(intent, CODIGO_REQUISICAO_COLA);
            }
        });


        // Criação do botão 'MOSTRA'
        mBotaoMostra = (Button) findViewById(R.id.botao_mostra_questoes);
        mBotaoMostra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        /*
          Acesso ao SQLites
          Feito o acesso ao SQLites e um tratamentos para erros
        */
                if (mQuestoesDb == null) return;
                if (mTextViewQuestoesArmazenadas == null) {
                    mTextViewQuestoesArmazenadas = (TextView) findViewById(R.id.texto_questoes_a_apresentar);
                } else {
                    mTextViewQuestoesArmazenadas.setText("");
                }
                Cursor cursor = mQuestoesDb.queryResposta(null, null);
                if (cursor != null) {
                    try {
                        if (cursor.getCount() == 0) {
                            mTextViewQuestoesArmazenadas.setText("Nada a apresentar");
                            Log.i("MSGS", "Nenhum resultado");
                        } else {
                            cursor.moveToFirst();
                            while (!cursor.isAfterLast()) {
                                int respostaCorreta = cursor.getInt(cursor.getColumnIndex(String.valueOf(QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_CORRETA)));
                                boolean respostaOferecida = cursor.getInt(cursor.getColumnIndex(String.valueOf(QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_OFERECIDA))) == 1;
                                boolean colou = cursor.getInt(cursor.getColumnIndex(String.valueOf(QuestoesDbSchema.RespostasTbl.Cols.RESPOSTA_OFERECIDA))) == 1;
                                mTextViewQuestoesArmazenadas.append(
                                        "\nResposta Correta: " +  respostaCorreta +
                                                "\nResposta Oferecida: " +  respostaOferecida +
                                                "\nColou: " +  colou +
                                                "\n--------------------------"
                                );
                                cursor.moveToNext();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("MSGS", "Erro ao acessar o banco de dados", e);
                    } finally {
                        cursor.close();
                    }
                } else {
                    Log.i("MSGS", "cursor nulo!");
                }
            }
        });

        mBotaoDeleta = (Button) findViewById(R.id.botao_deleta);
        mBotaoDeleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                  Acesso ao SQLite
                */
                if (mQuestoesDb != null) {
                    mQuestoesDb.removeBanco();
                    if (mTextViewQuestoesArmazenadas == null) {
                        mTextViewQuestoesArmazenadas = (TextView) findViewById(R.id.texto_questoes_a_apresentar);
                    }
                    mTextViewQuestoesArmazenadas.setText("");
                }
            }
        });

    }

    // Quando o usuário clica para ir para a próxima questão e ela é atualizada
    private void atualizaQuestao() {
        int questao = mBancoDeQuestoes[mIndiceAtual].getTextoRespostaId();
        mTextViewQuestao.setText(questao);
    }

    // Verifica na classe Questao se a resposta foi correta ou não
    private void verificaResposta(boolean respostaPressionada) {
        boolean respostaCorreta = mBancoDeQuestoes[mIndiceAtual].isRespostaCorreta();
        int idMensagemResposta = 0;
        int acertou = 0;
        boolean colou = false;

        if (mEhColador) {
            idMensagemResposta = R.string.toast_julgamento;
            colou = true;
        } else {
            if (respostaPressionada == respostaCorreta) {
                idMensagemResposta = R.string.toast_correto;
                acertou = 1;
            } else {
                idMensagemResposta = R.string.toast_incorreto;
                acertou = 0;
            }
        }
        if (mQuestoesDb == null) {
            mQuestoesDb = new QuestaoDB(getBaseContext());
        }
        mQuestoesDb.addResposta(acertou, respostaPressionada, colou);
        Toast.makeText(this, idMensagemResposta, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle instanciaSalva) {
        super.onSaveInstanceState(instanciaSalva);
        Log.i(TAG, "onSaveInstanceState()");
        instanciaSalva.putInt(CHAVE_INDICE, mIndiceAtual);
    }

    @Override
    protected void onActivityResult(int codigoRequisicao, int codigoResultado, Intent dados) {
        if (codigoResultado != Activity.RESULT_OK) {
            return;
        }
        if (codigoRequisicao == CODIGO_REQUISICAO_COLA) {
            if (dados == null) {
                return;
            }
            mEhColador = ColaActivity.foiMostradaResposta(dados);
        }
    }
}