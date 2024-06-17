package com.mrbysco.structurecompass.registry;

import com.mrbysco.structurecompass.Reference;
import com.mrbysco.structurecompass.component.StructureInfo;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class StructureComponents {
	public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Reference.MOD_ID);

	public static final Supplier<DataComponentType<ResourceLocation>> STRUCTURE = DATA_COMPONENT_TYPES.register("structure", () ->
			DataComponentType.<ResourceLocation>builder()
					.persistent(ResourceLocation.CODEC)
					.networkSynchronized(ResourceLocation.STREAM_CODEC)
					.build());

	public static final Supplier<DataComponentType<StructureInfo>> STRUCTURE_INFO = DATA_COMPONENT_TYPES.register("structure_info", () ->
			DataComponentType.<StructureInfo>builder()
					.persistent(StructureInfo.CODEC)
					.networkSynchronized(StructureInfo.STREAM_CODEC)
					.build());

}
