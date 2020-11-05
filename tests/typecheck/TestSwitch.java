//import org.checkerframework.checker.fenum.qual.Fenum;
import fenum.qual.Fenum;

class TestSwitch {
    @SuppressWarnings("fenum:assignment.type.incompatible")
        @Fenum("TEST") final int annotated = 3;

        @SuppressWarnings("fenum:assignment.type.incompatible")
        @Fenum("TEST") final int annotated2 = 6;


    void m1() {
        
        int plain = 9; // FenumUnqualified

        switch (plain) {
                // :: error: (switch.type.incompatible)
            case annotated:
            default:
        }
    }

        
    void m2() {
        int plain = 9; // FenumUnqualified

        // un-annotated still working
        switch (plain) {
            case 1:
            default:
        }
    }


    void m3() {
        switch (annotated) {
                // :: error: (switch.type.incompatible)
            case 45:
            default:
        }
    }


    void m4() {
        // annotated working
        switch (annotated) {
            case annotated2:
            default:
        }
    }
  
}
