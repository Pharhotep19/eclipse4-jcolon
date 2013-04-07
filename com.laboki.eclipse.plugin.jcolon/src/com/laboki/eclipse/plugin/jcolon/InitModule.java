package com.laboki.eclipse.plugin.jcolon;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

final class InitModule extends AbstractModule {

	@Override
	protected void configure() {
		this.registerEventBusListeners();
		this.bindConstant().annotatedWith(Names.named("true")).to(true);
		this.bindConstant().annotatedWith(Names.named("false")).to(false);
		// this.bind(Dialog.class).asEagerSingleton();
	}

	private void registerEventBusListeners() {
		this.bindListener(Matchers.any(), new TypeListener() {

			@Override
			public <I> void hear(final TypeLiteral<I> typeLiteral, final TypeEncounter<I> typeEncounter) {
				typeEncounter.register(new InjectionListener<I>() {

					@Override
					public void afterInjection(final I i) {
						EventBus.register(i);
					}
				});
			}
		});
	}
}
