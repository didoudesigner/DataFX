/**
 * Copyright (c) 2011, 2014, Jonathan Giles, Johan Vos, Hendrik Ebbers All
 * rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of DataFX, the website
 * javafxdata.org, nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.datafx.controller.flow.container;

import javafx.scene.layout.StackPane;
import io.datafx.controller.context.ViewContext;
import io.datafx.controller.flow.FlowContainer;

/**
 * Default implementation of the {@link FlowContainer} interface. The class will wrap a flow in a {@link StackPane}.
 */
public class DefaultFlowContainer implements FlowContainer<StackPane> {

    private final StackPane pane;

    /**
     * Creates a container that wraps the flow in the given {@link StackPane}
     * @param pane the pane that will be used as parent node for the flow
     */
    public DefaultFlowContainer(StackPane pane) {
        this.pane = pane;
    }

    /**
     * Default constructor that wraps the flow in a {@link StackPane}. The {@link StackPane} instance can be accessed by
     * calling {@link #getView()}.
     */
    public DefaultFlowContainer() {
        this(new StackPane());
    }

    @Override
    public StackPane getView() {
        return pane;
    }

    @Override
    public <U> void setViewContext(ViewContext<U> context) {
        pane.getChildren().clear();
        pane.getChildren().add(context.getRootNode());
    }
}
