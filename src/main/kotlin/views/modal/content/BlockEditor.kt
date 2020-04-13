package io.github.cottonmc.prefabmod.views.modal.content

import io.github.cottonmc.functionapi.api.content.block.BlockTemplate
import io.github.cottonmc.functionapi.blocks.templates.BlockTemplateImpl
import io.github.cottonmc.prefabmod.content.ContentManager


class BlockEditor(private val block: String,new:Boolean) : AbstractEditorBase<BlockTemplate>("", BlockTemplateImpl(),new) {

    override fun onBeforeShow() {
        super.onBeforeShow()
    }

    override fun saveAction() {
        ContentManager.save()
    }
}