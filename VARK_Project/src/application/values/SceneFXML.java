package application.values;

public enum SceneFXML {
    WINDOW {
        @Override
        public String toString() {
            return "resources/MainWindow.fxml";
        }
    },
    MENU {
        @Override
        public String toString() {
            return "resources/MainMenu.fxml";
        }
    },
    VIEW {
        @Override
        public String toString() {
            return "resources/SideView.fxml";
        }
    },
    SEARCH {
        @Override
        public String toString() {
            return "resources/SearchTerm.fxml";
        }
    },
    AUDIO {
        @Override
        public String toString() {
            return "resources/EditText.fxml";
        }
    },
    IMAGES {
        @Override
        public String toString() { return "resources/EditPictures.fxml"; }
    },
    CREATE {
        @Override
        public String toString() { return "resources/CreateNew.fxml"; }
    },
    TIP {
        @Override
        public String toString() { return "resources/StarTip.fxml"; }
    };
}
