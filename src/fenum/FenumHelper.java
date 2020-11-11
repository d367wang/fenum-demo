package fenum;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.VariableTree;

import fenum.qual.Fenum;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import fenum.qual.FenumPattern;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.TreeUtils;
import org.checkerframework.javacutil.ElementUtils;
import org.checkerframework.javacutil.TypesUtils;


import java.util.Map;


public class FenumHelper {
  private final AnnotatedTypeFactory atypeFactory; 

  public FenumHelper(AnnotatedTypeFactory atypeFactory) {
    this.atypeFactory = atypeFactory;
        
  }

    /**
     * Checks if the given variable is a fake enum declaration.
     *
     * @param node the variable to test
     * @return true if the given variable is a fake enum declaration
     */
    public boolean isFenumDeclaration(VariableTree node) {
        VariableElement varElement = TreeUtils.elementFromDeclaration(node);
        AnnotatedTypeMirror atm = atypeFactory.getAnnotatedType(node);
        return hasFenumAnnotation(atm)
            //&& ElementUtils.isPublic(varElement)
                && ElementUtils.isFinal(varElement)
                && ElementUtils.isStatic(varElement);
    }

    /**
     * Checks if the given annotated type contains {@link Fenum} annotation.
     *
     * @param atm {@link AnnotatedTypeMirror} to test
     * @return true if {@code atm} contains {@link Fenum} annotation
     */
    public static boolean hasFenumAnnotation(AnnotatedTypeMirror atm) {
        AnnotationMirror anno = atm.getAnnotation(Fenum.class);
        return anno != null;
    }

    /**
     * Gets {@code value} field of {@link Fenum} annotation.
     *
     * @param anno {@link Fenum} annotation
     * @return {@code value} option specified in the input annotation
     */
    public static String getFenumName(AnnotationMirror anno) {
        return AnnotationUtils.getElementValue(anno, "value", String.class, false);
    }

    /**
     * Gets {@code value} field of {@link Fenum} annotation.
     *
     * @param anno {@link Fenum} annotation
     * @return {@code value} option specified in the input annotation
     */
    public static FenumPattern getFenumPattern(AnnotationMirror anno) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> valmap;
        valmap = anno.getElementValues();

        for (ExecutableElement elem : valmap.keySet()) {
            if (elem.getSimpleName().contentEquals("pattern")) {
                AnnotationValue val = valmap.get(elem);
                switch (val.getValue().toString()) {
                    case "CONSECUTIVE":
                        return FenumPattern.CONSECUTIVE;
                    case "FLAG":
                        return FenumPattern.FLAG;
                }
            }
        }

        return FenumPattern.UNCHECKED;
    }

    /**
     * Tests if the given integer is a power of 2, which is required in the fake enum pattern of flags.
     *
     * @param value the integer to be tested
     * @return true if the integer is a power of 2
     */
    public static boolean isPowerOfTwo(int value) {
        return (value & (value - 1)) == 0;
    }

    /**
     * Gets value of the variable which is declared to be constant. (i.e. final static variable)
     *
     * @param node the variable to get constant value from
     * @param underlyingType the actual type of the variable
     * @return the constant value of the variable
     */
    public static Object getConstantValue(VariableTree node, TypeMirror underlyingType) {
        VariableElement varElement = TreeUtils.elementFromDeclaration(node);
        if (TypesUtils.isPrimitive(underlyingType)) {
            return varElement.getConstantValue();
        }
        final ExpressionTree initializer = node.getInitializer();
        assert initializer != null;
        if (initializer instanceof LiteralTree) {
            Object val = ((LiteralTree) initializer).getValue();
            return val instanceof String ? val : null;
        }
        return null;
    }

    public AnnotationMirror genereateFenumAnnotation(String type) {
        return genereateFenumAnnotation(type, FenumPattern.UNCHECKED);
    }

    public AnnotationMirror genereateFenumAnnotation(String type, FenumPattern pattern) {
        AnnotationBuilder annotationBuilder = new AnnotationBuilder(atypeFactory.getProcessingEnv(), Fenum.class);
        annotationBuilder.setValue("value", type);
        return annotationBuilder.build();
    }
}
