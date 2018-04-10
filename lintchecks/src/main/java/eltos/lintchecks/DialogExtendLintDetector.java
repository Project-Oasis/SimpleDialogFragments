/*
 *  Copyright 2018 Philipp Niedermayer (github.com/eltos)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eltos.lintchecks;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;

import org.jetbrains.uast.UClass;

import java.util.Collections;
import java.util.List;

/**
 * This code checks classes extending a SimpleDialog for the issues listed below
 */

public class DialogExtendLintDetector extends Detector implements Detector.UastScanner {

    /**
     * Overwriting of static build() method is required when extending a SimpleDialog
     *
     */
    private static String BUILD_OVERWRITE_MESSAGE = "Class extends SimpleDialog but does not " +
            "implement a static `build` method.";
    public static final Issue BUILD_OVERWRITE = Issue.create("BuildOverwrite",
            "Missing build method",
            "This check checks for classes that extend a `SimpleDialog` " +
                    "but do not implement a static `build` method.\n" +
                    "\n" +
                    "Implementing the method is required, as a call to the inherited build method " +
                    "would otherwise unintentionally create an instance of the parent class.\n",
            Category.USABILITY, 6, Severity.WARNING,
            new Implementation(DialogExtendLintDetector.class, Scope.JAVA_FILE_SCOPE));









    @Override
    public List<String> applicableSuperClasses() {
        return Collections.singletonList("eltos.simpledialogfragment.SimpleDialog");
    }

    @Override
    public void visitClass(JavaContext context, UClass declaration) {
        PsiModifierList classModifiers = declaration.getModifierList();
        if (classModifiers == null || !classModifiers.hasModifierProperty("abstract")) {
            // check for static build method
            boolean hasBuildMethod = false;
            for (PsiMethod method : declaration.getMethods()) {
                if ("build".equals(method.getName()) && method.getModifierList()
                        .hasModifierProperty("static")) {
                    hasBuildMethod = true;
                    break;
                }
            }
            if (!hasBuildMethod){
                context.report(BUILD_OVERWRITE, context.getLocation(declaration.getExtendsList()),
                        BUILD_OVERWRITE_MESSAGE);
            }

        }
    }

}
