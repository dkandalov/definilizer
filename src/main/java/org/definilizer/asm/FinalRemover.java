package org.definilizer.asm;

import org.objectweb.asm.*;

/**
 * User: dima
 * Date: Sep 6, 2008
 * Time: 11:26:13 PM
 */
public class FinalRemover {
    public byte[] process(final byte[] input) {
        ClassWriter classWriter = new ClassWriter(false);
        ClassAdapter classAdapter = new ClassAdapter(classWriter) {
            @Override public void visit(final int version, final int access, final String name, final String signature,
                                        final String superName, final String[] interfaces) {
                // remove final from class header
                super.visit(version, removeFinalFrom(access), name, signature, superName, interfaces);
            }

            @Override public FieldVisitor visitField(final int access, final String name, final String desc,
                                                     final String signature, final Object value) {
                // remove final from fields' headers
                return super.visitField(removeFinalFrom(access), name, desc, signature, value);
            }
        };
        ClassReader classReader = new ClassReader(input);
        classReader.accept(classAdapter, false);
        return classWriter.toByteArray();
    }

    private static int removeFinalFrom(final int access) {
        return access & (~Opcodes.ACC_FINAL);
    }
}
