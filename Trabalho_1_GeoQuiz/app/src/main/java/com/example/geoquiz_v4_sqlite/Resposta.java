package com.example.geoquiz_v4_sqlite;

import java.util.UUID;

public class Resposta {
    private UUID mId;
    private int mRespostaCorreta;
    private boolean mRespostaOferecida;
    private boolean mColou;;

    public Resposta(int RespostaCorreta, boolean RespostaOferecida, boolean Colou) {
        this.mRespostaCorreta = RespostaCorreta;
        this.mRespostaOferecida = RespostaOferecida;
        this.mColou = Colou;
        mId = UUID.randomUUID();
    }

    UUID getId(){return mId;};

    public int getRespostaCorreta() {
        return mRespostaCorreta;
    }

    public void setRespostaCorreta(int respostaCorreta) {
        mRespostaCorreta = respostaCorreta;
    }

    public boolean getRespostaOferecida() {
        return mRespostaOferecida;
    }

    public void setRespostaOferecida(boolean RespostaOferecida) {
        mRespostaOferecida = RespostaOferecida;
    }

    public boolean getColou() {
        return mColou;
    }

    public void setColou(boolean Colou) {
        mColou = Colou;
    }
}
