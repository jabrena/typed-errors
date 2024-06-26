/**
 * This module defines utilities for functional programming.
 */
module info.jab.fp.util {
    exports info.jab.fp.util.either;
    exports info.jab.fp.util.raise;
    exports info.jab.fp.util.result;

    requires transitive jakarta.annotation;
    requires transitive org.slf4j;

    //modules used in tests
    requires java.net.http;
}
