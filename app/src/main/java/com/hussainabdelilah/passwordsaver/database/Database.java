package com.hussainabdelilah.passwordsaver.database;


public class Database {

    public static final class UsernamesTable {
        public static final String TABLE_NAME = "usernames";
        public static final class Columns{
            public static final String ID = "id";
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
        }
    }

    public static final class CategoryTable {
        public static final String TABLE_NAME = "category";
        public static final class Columns{
            public static final String ID = "id";
            public static final String ID_USERNAME = "id_username";
            public static final String CATEGORY = "category";
        }
    }

    public static final class DataTable {
        public static final String TABLE_NAME = "data_table";
        public static final class Columns {
            public static final String ID = "id";
            public static final String ID_USERNAME = "id_username";
            public static final String ID_CATEGORY = "id_category";
            public static final String ACCOUNT = "account";
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
        }
    }
}
