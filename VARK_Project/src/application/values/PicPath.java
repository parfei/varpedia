package application.values;

import application.Main;

public enum PicPath {
    DEFAULT{
        @Override
        public String toString() { return String.valueOf(Main.class.getResource("resources/images")); }
    },
    MENU{
        @Override
        public String toString() { return String.valueOf(Main.class.getResource("resources/images/menu")); }
    },
    VIEW{
        @Override
        public String toString() { return String.valueOf(Main.class.getResource("resources/images/view")); }
    },
    SEARCH{
        @Override
        public String toString() { return String.valueOf(Main.class.getResource("resources/images/search")); }
    },
    AUDIO{
        @Override
        public String toString() { return String.valueOf(Main.class.getResource("resources/images/edit_text")); }
    },
    FINAL{
        @Override
        public String toString() { return String.valueOf(Main.class.getResource("resources/images/create_new")); }
    };
}
