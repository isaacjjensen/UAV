package edu.und.seau.di.components;

import dagger.Component;
import edu.und.seau.di.module.InteractionModule;
import edu.und.seau.presentation.presenters.ConnectionScreenPresenter;
import edu.und.seau.presentation.presenters.MainScreenPresenter;

@Component(modules = InteractionModule.class)
public interface PresentationComponent {
    MainScreenPresenter getMainPresenter();
    ConnectionScreenPresenter getConnectionScreenPresenter();

}
