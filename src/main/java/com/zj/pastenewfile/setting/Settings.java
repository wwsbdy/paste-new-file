package com.zj.pastenewfile.setting;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.zj.pastenewfile.vo.StateVo;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 * @author : jie.zhou
 * @date : 2025/9/25
 */
@State(name = "PasteNewFileSettings", storages = @Storage("pasteNewFile/PasteNewFileSettings.xml"))
public final class Settings implements PersistentStateComponent<StateVo> {


    private StateVo myState;

    public static Settings getInstance() {
//        return ServiceManager.getService(Settings.class);
        return ApplicationManager.getApplication().getService(Settings.class);
    }


    @NotNull
    @Override
    public synchronized StateVo getState() {
        if (Objects.isNull(myState) || myState.isEmpty()) {
            return myState = init();
        }
        return myState;
    }

    @Override
    public void loadState(@NotNull StateVo state) {
        myState = state;
    }

    private StateVo init() {
        return new StateVo();
    }
}
