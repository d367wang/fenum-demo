//import testlib.wholeprograminference.qual.Top;
import org.checkerframework.checker.fenum.qual.FenumTop;
import org.checkerframework.checker.fenum.qual.Fenum;

public class TestVoid {
	private @FenumTop int privateDeclaredField;
	public @FenumTop int publicDeclaredField;

	public void foo() {
	}

	public void bar() {
		foo();
	}

	void testFields() {
		// :: error: (argument.type.incompatible)
		expectsSibling1(privateDeclaredField);
		// :: error: (argument.type.incompatible)
		expectsSibling1(publicDeclaredField);
	}

	void expectsSibling1(@Fenum("A") int t) {}

	void expectsSibling2(@Fenum("B") int t) {}
}
