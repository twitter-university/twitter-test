package com.twitter.university.android.datasender;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class SenderContract {
    private SenderContract() { }

    public static final String AUTHORITY = "com.twitter.university.android.datasender";

    public static final Uri BASE_URI = new Uri.Builder()
        .scheme(ContentResolver.SCHEME_CONTENT)
        .authority(AUTHORITY)
        .build();

    public static final class Files {
        private Files() {}

        public static final String TABLE = "files";

        public static final String MIME_TYPE = "image/jpeg";

        public static final Uri URI = BASE_URI.buildUpon().appendPath(TABLE).build();
    }

    public static final class Extras {
        private Extras() {}

        public static final String TABLE = "extras";

        public static final Uri URI = BASE_URI.buildUpon().appendPath(TABLE).build();

        public static final String MIME_TYPE = "image/jpeg";

        public static final class Columns {
            private Columns() {}

            static final String ID = BaseColumns._ID;
            static final String EXTRA = "data";
        }
    }
}
