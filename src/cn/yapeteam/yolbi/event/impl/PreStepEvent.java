package cn.yapeteam.yolbi.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.yapeteam.yolbi.event.Event;

@Getter
@Setter
@AllArgsConstructor
public class PreStepEvent extends Event {

    private float height;

}