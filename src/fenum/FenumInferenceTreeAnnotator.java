package fenum;

import checkers.inference.InferenceAnnotatedTypeFactory;
import checkers.inference.InferenceMain;
import checkers.inference.InferenceTreeAnnotator;
import checkers.inference.InferrableChecker;
import checkers.inference.model.ConstantSlot;
import checkers.inference.model.Slot;
import checkers.inference.qual.VarAnnot;
import checkers.inference.SlotManager;
import checkers.inference.VariableAnnotator;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.VariableTree;

import fenum.qual.Fenum;

import java.util.Set;
import javax.lang.model.element.AnnotationMirror;

import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationBuilder;


public class FenumInferenceTreeAnnotator extends InferenceTreeAnnotator {

  private final VariableAnnotator variableAnnotator;
  private final AnnotatedTypeFactory realTypeFactory;
  private final SlotManager slotManager;

    public FenumInferenceTreeAnnotator(InferenceAnnotatedTypeFactory atypeFactory,
                                          InferrableChecker realChecker, AnnotatedTypeFactory realAnnotatedTypeFactory,
                                          VariableAnnotator variableAnnotator, SlotManager slotManager) {
        super(atypeFactory, realChecker, realAnnotatedTypeFactory, variableAnnotator, slotManager);

        this.variableAnnotator = variableAnnotator;
        this.realTypeFactory = realAnnotatedTypeFactory;
        this.slotManager = InferenceMain.getInstance().getSlotManager();
    }


    @Override
    public Void visitVariable(final VariableTree varTree, final AnnotatedTypeMirror atm) {
      super.visitVariable(varTree, atm);

      FenumHelper helper = new FenumHelper(realTypeFactory);
      if (helper.isFenumDeclaration(varTree)) {
        
        ExpressionTree initializer = varTree.getInitializer();
        AnnotatedTypeMirror rhsATM = atypeFactory.getAnnotatedType(initializer);

/*
        System.out.println("initializer ATM: " + rhsATM.toString());
        final Slot slot = slotManager.getVariableSlot(atm);
        AnnotationMirror anno = this.slotManager.getAnnotation(slot);
        rhsATM.replaceAnnotation(anno);
        */

        Set<AnnotationMirror> annos = atm.getAnnotations();
        rhsATM.replaceAnnotations(annos);

        //System.out.println("replace initializer ATM with: " + rhsATM.toString());
                
      }

      return null;
          
    }
}
