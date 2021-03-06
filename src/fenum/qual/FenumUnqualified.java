package fenum.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.LiteralKind;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TypeUseLocation;
import org.checkerframework.framework.qual.QualifierForLiterals;

/**
 * An unqualified type. Such a type is incomparable to (that is, neither a subtype nor a supertype
 * of) any fake enum type.
 *
 * <p>This annotation may not be written in source code; it is an implementation detail of the
 * checker.
 *
 * @checker_framework.manual #fenum-checker Fake Enum Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({}) // empty target prevents programmers from writing this in a program
@SubtypeOf({FenumTop.class})
@DefaultQualifierInHierarchy
@DefaultFor(TypeUseLocation.EXCEPTION_PARAMETER)
@QualifierForLiterals({
    //LiteralKind.BOOLEAN,
    //LiteralKind.CHAR,
    //LiteralKind.DOUBLE,
    //LiteralKind.FLOAT,
        LiteralKind.INT,
            //LiteralKind.LONG,
            //LiteralKind.NULL,
        LiteralKind.STRING,
        
        })
public @interface FenumUnqualified {}
