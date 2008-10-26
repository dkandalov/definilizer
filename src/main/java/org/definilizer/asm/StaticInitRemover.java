package org.definilizer.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * User: dima
 * Date: Sep 12, 2008
 * Time: 12:18:18 AM
 */
public class StaticInitRemover { // fixme improve class
    public byte[] process(byte[] input) {
        ClassWriter classWriter = new ClassWriter(false);
        ClassAdapter classAdapter = new ClassAdapter(classWriter) {
            @Override public MethodVisitor visitMethod(final int access, final String name, final String desc,
                                                       final String signature, final String[] exceptions) {
                final MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
                if ("<clinit>".equals(name)) {
                    return new StaticInitRemovingAdapter(methodVisitor);
                } else {
                    return methodVisitor;
                }
            }
        };
        ClassReader classReader = new ClassReader(input);
        classReader.accept(classAdapter, false);
        return classWriter.toByteArray();
    }

    public static void printClass(byte[] input) {
        ClassReader classReader = new ClassReader(input);
        classReader.accept(new TraceClassVisitor(new PrintWriter(System.out)), false);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        final byte[] bytes = loadClassAsBytes();

        printClass(bytes);

        final byte[] bytes1 = new StaticInitRemover().process(bytes);
        final byte[] bytes2 = new FinalRemover().process(bytes1);

        System.out.println("==============================================");
        System.out.println("==============================================");
        System.out.println("==============================================");
        printClass(bytes2);

        // try to create changed class
        new ClassLoader() {
            @Override public Class<?> loadClass(final String name) throws ClassNotFoundException {
                if (name.isEmpty()) {
                    return defineClass(null, bytes2, 0, bytes2.length);
                } else {
                    return getSystemClassLoader().loadClass(name);
                }
            }
        }.loadClass("");
    }

    private static byte[] loadClassAsBytes() throws IOException {
        final File file = new File("/home/dima/IdeaProjects/manual_dep_inj/out/production/manual_dep_inj/home/lib/FinalStaticDataProcessor.class");
        final FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        int counter = 0;
        int i;
        while ((i = inputStream.read()) != -1) {
            bytes[counter++] = (byte) i;
        }
        return bytes;
    }

    private static class StaticInitRemovingAdapter extends MethodAdapter {
        private StaticInitSearchingStateMachine machine;

        public StaticInitRemovingAdapter(final MethodVisitor mv) {
            super(mv);
            machine = new StaticInitSearchingStateMachine("home/lib/FinalStaticDataProcessor");
        }

        @Override public void visitTypeInsn(final int opcode, final String desc) {
            System.out.println(desc);
            if (opcode == Opcodes.NEW) {
                machine.gotNew(desc);
                if (machine.skipInstruction()) return;
            }
            super.visitTypeInsn(opcode, desc);
        }

        @Override public void visitInsn(final int opcode) {
            if (opcode == Opcodes.DUP) {
                machine.gotDup();
                if (machine.skipInstruction()) return;
            }
            super.visitInsn(opcode);
        }

        @Override
        public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
            if (opcode == Opcodes.PUTSTATIC) {
                machine.gotPutStatic(owner);
                if (machine.skipInstruction()) return;
            }
            super.visitFieldInsn(opcode, owner, name, desc);
        }

        @Override
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
            if (opcode == Opcodes.INVOKESPECIAL) {
                machine.gotInvokeSpecial(owner);
                if (machine.skipInstruction()) return;
            }
            super.visitMethodInsn(opcode, owner, name, desc);
        }

        @Override public void visitIntInsn(final int opcode, final int operand) {
            machine.gotSomethingWrong();
            super.visitIntInsn(opcode, operand);
        }

        @Override public void visitVarInsn(final int opcode, final int var) {
            machine.gotSomethingWrong();
            super.visitVarInsn(opcode, var);
        }

        @Override public void visitJumpInsn(final int opcode, final Label label) {
            machine.gotSomethingWrong();
            super.visitJumpInsn(opcode, label);
        }

        @Override public void visitLdcInsn(final Object cst) {
            machine.gotSomethingWrong();
            super.visitLdcInsn(cst);
        }

        @Override public void visitIincInsn(final int var, final int increment) {
            machine.gotSomethingWrong();
            super.visitIincInsn(var, increment);
        }

        @Override public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label[] labels) {
            machine.gotSomethingWrong();
            super.visitTableSwitchInsn(min, max, dflt, labels);
        }

        @Override public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
            machine.gotSomethingWrong();
            super.visitLookupSwitchInsn(dflt, keys, labels);
        }

        @Override public void visitMultiANewArrayInsn(final String desc, final int dims) {
            machine.gotSomethingWrong();
            super.visitMultiANewArrayInsn(desc, dims);
        }
    }
}
