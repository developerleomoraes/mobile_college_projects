package com.example.agenda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {
    private CompromissoDB compromissoDB;
    private String dataSelecionada;
    private String horaSelecionada;
    private EditText mDescricao;
    private TextView compromissosTextView;

    void setDataSelecionada(String dataSelecionada) {
        this.dataSelecionada = dataSelecionada;
    }

    void setHoraSelecionada(String horaSelecionada) {
        this.horaSelecionada = horaSelecionada;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDescricao = findViewById(R.id.descricao_input);
        Button botaoCriar = findViewById(R.id.btn_criar);

        botaoCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descricao = mDescricao.getText().toString();
                criaCompromisso(dataSelecionada, horaSelecionada, descricao);
            }
        });

        compromissosTextView = findViewById(R.id.textView_scroll);
        Button botaoHoje = findViewById(R.id.btn_hoje);

        botaoHoje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instaciaDB();
                mostraCompromissos(getDataDeHoje());
            }
        });
    }

    public void mostraDialogoTimePicker(View v) {
        DialogFragment newFragment = new FragmentoTimePicker();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void mostraDialogoDatePicker(View v) {
        DialogFragment newFragment = new FragmentoDatePicker();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void criaCompromisso(String data, String hora, String descricao) {
        if (data != null && hora != null) {
            Compromisso compromisso = new Compromisso(data, hora, descricao);
            compromissoDB.addCompromisso(compromisso);
            mostraCompromissos(getDataDeHoje());
        }
    }

    void mostraCompromissos(String data) {
        String clausulaWhere = CompromissosDBSchema.CompromissosTbl.Cols.DATA + " = ?";
        String[] argsWhere = new String[]{data};
        String compromissos = compromissoDB.listaCompromissos(clausulaWhere, argsWhere);

        compromissosTextView.setText(compromissos);
    }

    private void instaciaDB() {
        if (compromissoDB == null) {
            compromissoDB = new CompromissoDB(this);
        }
    }

    private String getDataDeHoje() {
        LocalDate dataHoje = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        return dataHoje.format(formatter);
    }
}
