package application.values;

public enum SceneFXML {
    WINDOW{
        @Override
        public String toString() {
            return "resources/MainWindow.fxml";
        }
    },
    MENU{
        @Override
        public String toString() {
            return "resources/MainMenu.fxml";
        }
    },
    VIEW{
        @Override
        public String toString() {
            return "resources/View.fxml";
        }
    },
    SEARCH{
        @Override
        public String toString() {
            return "resources/SearchTerm.fxml";
        }
    },
    AUDIO{
        @Override
        public String toString() {
            return "resources/EditText.fxml";
        }
    },
    CREATE{
        @Override
        public String toString() {
            return "resources/CreateNew.fxml";
        }
    };
}
