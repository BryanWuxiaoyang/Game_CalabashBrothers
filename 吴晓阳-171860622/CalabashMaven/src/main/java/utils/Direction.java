package utils;

import java.io.Serializable;

public enum Direction {
    UP{
        @Override
        public boolean isUp() {
            return true;
        }

        @Override
        public boolean isDown() {
            return false;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return false;
        }
    },
    UPRIGHT{
        @Override
        public boolean isUp() {
            return true;
        }

        @Override
        public boolean isDown() {
            return false;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }
    },
    RIGHT{
        @Override
        public boolean isUp() {
            return false;
        }

        @Override
        public boolean isDown() {
            return false;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }
    },
    DOWNRIGHT{
        @Override
        public boolean isUp() {
            return false;
        }

        @Override
        public boolean isDown() {
            return true;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }
    },
    DOWN{
        @Override
        public boolean isUp() {
            return false;
        }

        @Override
        public boolean isDown() {
            return true;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return false;
        }
    },
    DOWNLEFT{
        @Override
        public boolean isUp() {
            return false;
        }

        @Override
        public boolean isDown() {
            return true;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }
    },
    LEFT{
        @Override
        public boolean isUp() {
            return false;
        }

        @Override
        public boolean isDown() {
            return false;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }
    },
    UPLEFT{
        @Override
        public boolean isUp() {
            return true;
        }

        @Override
        public boolean isDown() {
            return false;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }
    },
    MIDDLE{
        @Override
        public boolean isUp() {
            return false;
        }

        @Override
        public boolean isDown() {
            return false;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return false;
        }
    };

    public abstract boolean isUp();

    public abstract boolean isDown();

    public abstract boolean isLeft();

    public abstract boolean isRight();
}
