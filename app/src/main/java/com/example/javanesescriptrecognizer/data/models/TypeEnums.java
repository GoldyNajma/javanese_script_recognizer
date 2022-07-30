package com.example.javanesescriptrecognizer.data.models;

//public class TypeEnums {
//    public enum GroupType {
//        aksara, sandhangan, pasangan,
//    }
//
//    public enum SubGroupType {
//        wyanjana, murda, rekan, swara, ganten
//    }
//
//    public enum WyanjanaType {
//        ha, na, ca, ra, ka, da, ta, sa, wa, la,
//        pa, dha, ja, ya, nya, ma, ga, ba, tha, nga,
//    }
//
//    public enum MurdaType {
//        na, ka, ta, sa, pa, nya, ga, ba,
//    }
//
//    public enum RekanType {
//        kha, gha, dza, fa, za
//    }
//
//    public enum SwaraType {
//        a, i, u, e, o,
//    }
//
//    public enum GantenType {
//        paCerek, ngaLelet, pasanganPaCerek,
//    }
//
//    public enum SandhanganType {
//        wulu, suku, pepet, taling, tarung, pangkon,
//        wigyan, layar, cecak, cakra, ceret, pengkal,
//    }
//}

public class TypeEnums {
    public enum GroupType {
        aksara, sandhangan, pasangan,
    }

    public enum SubGroupType {
        wyanjana, murda, rekan, swara, ganten
    }

    public enum WyanjanaType {
        ha("ꦲ", "꧀ꦲ"),
        na("ꦤ", "꧀ꦤ"),
        ca("ꦕ", "꧀ꦕ"),
        ra("ꦫ", "꧀ꦫ"),
        ka("ꦏ", "꧀ꦏ"),
        da("ꦢ", "꧀ꦢ"),
        ta("ꦠ", "꧀ꦠ"),
        sa("ꦱ", "꧀ꦱ"),
        wa("ꦮ", "꧀ꦮ"),
        la("ꦭ", "꧀ꦭ"),
        pa("ꦥ", "꧀ꦥ"),
        dha("ꦣ", "꧀ꦣ"),
        ja("ꦗ", "꧀ꦗ"),
        ya("ꦪ", "꧀ꦪ"),
        nya("ꦚ", "꧀ꦚ"),
        ma("ꦩ", "꧀ꦩ"),
        ga("ꦒ", "꧀ꦒ"),
        ba("ꦧ", "꧀ꦧ"),
        tha("ꦛ", "꧀ꦛ"),
        nga("ꦔ", "꧀ꦔ");

        private final String aksara;
        private final String pasangan;

        WyanjanaType(String aksara, String pasangan) {
            this.aksara = aksara;
            this.pasangan = pasangan;
        }

        public String getAksara() {
            return aksara;
        }

        public String getPasangan() {
            return pasangan;
        }
    }

    public enum MurdaType {
        na("ꦟ", "꧀ꦟ"),
        ka("ꦑ", "꧀ꦑ"),
        ta("ꦡ", "꧀ꦡ"),
        sa("ꦯ", "꧀ꦯ"),
        pa("ꦦ", "꧀ꦦ"),
        nya("ꦘ", "꧀ꦘ"),
        ga("ꦓ", "꧀ꦓ"),
        ba("ꦨ", "꧀ꦨ");

        private final String aksara;
        private final String pasangan;

        MurdaType(String aksara, String pasangan) {
            this.aksara = aksara;
            this.pasangan = pasangan;
        }

        public String getAksara() {
            return aksara;
        }

        public String getPasangan() {
            return pasangan;
        }
    }

    public enum RekanType {
        kha("ꦏ꦳", "꧀ꦏ꦳"),
        gha("ꦒ꦳", "꧀ꦒ꦳"),
        dza("ꦢ꦳", "꧀ꦢ꦳"),
        fa("ꦥ꦳", "꧀ꦥ꦳"),
        za("ꦗ꦳", "꧀ꦗ꦳");

        private final String aksara;
        private final String pasangan;

        RekanType(String aksara, String pasangan) {
            this.aksara = aksara;
            this.pasangan = pasangan;
        }

        public String getAksara() {
            return aksara;
        }

        public String getPasangan() {
            return pasangan;
        }
    }

    public enum SwaraType {
        a("ꦄ"), i("ꦆ"), u("ꦈ"), e("ꦌ"), o("ꦎ");

        private final String aksara;

        SwaraType(String aksara) {
            this.aksara = aksara;
        }

        public String getAksara() {
            return aksara;
        }
    }

    public enum GantenType {
        paCerek("ꦉ"), ngaLelet("ꦊ"), pasanganPaCerek("꧀ꦉ");

        private final String aksara;

        GantenType(String aksara) {
            this.aksara = aksara;
        }

        public String getAksara() {
            return aksara;
        }
    }

    public enum SandhanganType {
        wulu("ꦶ"),
        suku("ꦸ"),
        pepet("ꦼ"),
        taling("ꦺ"),
        tarung("ꦴ"),
        pangkon("꧀"),
        wigyan("ꦃ"),
        layar("ꦂ"),
        cecak("ꦁ"),
        cakra("ꦿ"),
        ceret("ꦽ"),
        pengkal("ꦾ");

        private final String sandhangan;

        SandhanganType(String sandhangan) {
            this.sandhangan = sandhangan;
        }

        public String getSandhangan() {
            return sandhangan;
        }
    }
}

