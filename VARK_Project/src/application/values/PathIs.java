package application.values;

import application.PathCD;

public enum PathIs {
    CREATIONS{
        @Override
        public String toString() {
            return PathCD.getPathInstance().getPath() + "/mydir/creations/creations";
        }
    },
    FAVOURITES{
        @Override
        public String toString() {
            return PathCD.getPathInstance().getPath() + "/mydir/creations/favourites";
        }
    },
    EXTRA{
        @Override
        public String toString() {
            return PathCD.getPathInstance().getPath() + "/mydir/.extra";
        }
    },
    TEMP{
        @Override
        public String toString() {
            return PathCD.getPathInstance().getPath() + "/mydir/.temp";
        }
    }
}
