package com.example.javanesescriptrecognizer.data.models;

import androidx.annotation.NonNull;

import com.example.javanesescriptrecognizer.data.models.TypeEnums.*;

public class AksaraClasses {
    public abstract static class JavaneseScript {
        public abstract GroupType getGroupType();
        public abstract SubGroupType getSubGroupType();
        public abstract Enum getTypeEnum();
        public abstract String getReading();
        public abstract String getUnicode();

        @NonNull
        public String toString() {
            String groupTypeString = getGroupType().name();
            String subGroupTypeString = "";
            String typeEnumString = "." + getTypeEnum().name();

            if (getSubGroupType() != null) {
                subGroupTypeString = "." + getSubGroupType().name();
            }

            return groupTypeString + subGroupTypeString + typeEnumString;
        }

//        String toString() => '${describeTypeEnum(groupType)}'
//                '${subGroupType == null ? '' : '.'}${describeTypeEnum(subGroupType)}'
//                '.${describeTypeEnum(type)}';
    }

    public abstract static class Wyanjana extends JavaneseScript {
        private final WyanjanaType _type;

        public Wyanjana(WyanjanaType type) {
            _type = type;
        }

        public SubGroupType getSubGroupType() {
            return SubGroupType.wyanjana;
        }

        public WyanjanaType getTypeEnum() {
            return _type;
        }

        public String getReading() {
            return _type.name();
        }
    }

    public static class WyanjanaAksara extends Wyanjana {
        public WyanjanaAksara(WyanjanaType type) {
            super(type);
        }

        public GroupType getGroupType() {
            return GroupType.aksara;
        }

        public String getUnicode() {
            return getTypeEnum().getAksara();
        }
    }

    public static class WyanjanaPasangan extends Wyanjana {
        public WyanjanaPasangan(WyanjanaType type) {
            super(type);
        }

        public GroupType getGroupType() {
            return GroupType.pasangan;
        }

        public String getUnicode() {
            return getTypeEnum().getPasangan();
        }
    }

    public abstract static class Murda extends JavaneseScript {
        private final MurdaType _type;

        public Murda(MurdaType type) {
            _type = type;
        }

        public SubGroupType getSubGroupType() {
            return SubGroupType.murda;
        }

        public MurdaType getTypeEnum() {
            return _type;
        }

        public String getReading() {
            String reading = _type.name();

            return reading.substring(0, 1).toUpperCase() + reading.substring(1);
        }
    }

    public static class MurdaAksara extends Murda {
        public MurdaAksara(MurdaType type) {
            super(type);
        }

        public GroupType getGroupType() {
            return GroupType.aksara;
        }

        public String getUnicode() {
            return getTypeEnum().getAksara();
        }
    }

    public static class MurdaPasangan extends Murda {
        public MurdaPasangan(MurdaType type) {
            super(type);
        }

        public GroupType getGroupType() {
            return GroupType.pasangan;
        }

        public String getUnicode() {
            return getTypeEnum().getPasangan();
        }
    }

    public abstract static class Rekan extends JavaneseScript {
        private final RekanType _type;

        public Rekan(RekanType type) {
            _type = type;
        }

        public SubGroupType getSubGroupType() {
            return SubGroupType.rekan;
        }

        public RekanType getTypeEnum() {
            return _type;
        }

        public String getReading() {
            return _type.name();
        }
    }

    public static class RekanAksara extends Rekan {
        public RekanAksara(RekanType type) {
            super(type);
        }

        public GroupType getGroupType() {
            return GroupType.aksara;
        }

        public String getUnicode() {
            return getTypeEnum().getAksara();
        }
    }

    public static class RekanPasangan extends Rekan {
        public RekanPasangan(RekanType type) {
            super(type);
        }

        public GroupType getGroupType() {
            return GroupType.pasangan;
        }

        public String getUnicode() {
            return getTypeEnum().getPasangan();
        }
    }

    public static class Swara extends JavaneseScript {
        private final SwaraType _type;

        public Swara(SwaraType type) {
            _type = type;
        }

        public GroupType getGroupType() {
            return GroupType.aksara;
        }

        public SubGroupType getSubGroupType() {
            return SubGroupType.swara;
        }

        public SwaraType getTypeEnum() {
            return _type;
        }

        public String getReading() {
            String reading = _type.name();

            return reading.substring(0, 1).toUpperCase() + reading.substring(1);
        }

        public String getUnicode() {
            return getTypeEnum().getAksara();
        }
    }

    public static class Ganten extends JavaneseScript{
        private final GantenType _type;

        public Ganten(GantenType type) {
            _type = type;
        }

        public GroupType getGroupType() {
            return _type == GantenType.pasanganPaCerek ? GroupType.pasangan : GroupType.aksara;
        }

        public SubGroupType getSubGroupType() {
            return SubGroupType.ganten;
        }

        public GantenType getTypeEnum() {
            return _type;
        }

        public String getReading() {
            switch(_type) {
                case paCerek:
                case pasanganPaCerek:
                    return "re";
                case ngaLelet:
                    return "le";
                default:
                    return "";
            }
        }

        public String getUnicode() {
            return getTypeEnum().getAksara();
        }
    }

    public static class Sandhangan extends JavaneseScript {
        private final SandhanganType _type;

        public Sandhangan(SandhanganType type) {
            _type = type;
        }

        public GroupType getGroupType() {
            return GroupType.sandhangan;
        }

        public SubGroupType getSubGroupType() {
            return null;
        }

        public SandhanganType getTypeEnum() {
            return _type;
        }

        public String getReading() {
            switch(_type) {
                case wulu:
                    return "i";
                case suku:
                    return "u";
                case taling:
                    return "è";
                case pepet:
                    return "e";
                case tarung:
                    return "o";
                case wigyan:
                    return "h";
                case layar:
                    return "r";
                case cecak:
                    return "ng";
                case cakra:
                    return "ra";
                case ceret:
                    return "re";
                case pengkal:
                    return "ya";
                case pangkon:
                default:
                    return "";
            }
        }

        public String getUnicode() {
            return getTypeEnum().getSandhangan();
        }
    }
}
