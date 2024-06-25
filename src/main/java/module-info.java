/**
 * This module defines utilities for functional programming.
 */
module info.jab.fp.util {
    exports info.jab.fp.util;
    exports info.jab.fp.util.raise;

    requires transitive jakarta.annotation;
    requires org.slf4j;

    //modules used in tests
    requires java.net.http;
}
