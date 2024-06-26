/**
 * This module defines utilities for functional programming.
 */
module info.jab.fp.util {
    exports info.jab.util.either;
    exports info.jab.util.raise;
    exports info.jab.util.result;

    requires transitive jakarta.annotation;
    requires transitive org.slf4j;

    //modules used in tests
    requires java.net.http;
}
