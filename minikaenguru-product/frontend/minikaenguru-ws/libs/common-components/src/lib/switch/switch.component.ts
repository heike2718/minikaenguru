import { Component, EventEmitter, Input, forwardRef, HostBinding, Output } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'mk-switch',
  templateUrl: './switch.component.html',
  styleUrls: ['./switch.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SwitchComponent),
      multi: true
    }
  ]
})
export class SwitchComponent implements ControlValueAccessor {

  private ownId = '';

  @Input('value') _value = false;

  @Output()
  switchClicked: EventEmitter<boolean> = new EventEmitter<boolean>();
  
  // referenz auf eine möglicherweise von der parent-component gesetzte id.
  @HostBinding('attr.id')
  externalId: string | null = '';

  @Input()
  set id(value: string) {
    this.ownId = value;

    // setzt die extern gesetzte id des Elements null, damit es nicht 2 ids hat.
    this.externalId = null;
  }

  get id() {
    return this.ownId;
  }


  
  onChange: any = () => {};
  
  onTouched: any = () => {};

  get value() {
    return this._value;
  }

  set value(val) {
    this._value = val;
    this.onChange(val);
    this.onTouched();
  }

  constructor() {}

  registerOnChange(fn: any) {
    this.onChange = fn;
  }

  writeValue(value: boolean) {
    if (value) {
      this.value = value;
    }
  }

  registerOnTouched(fn: any) {
    this.onTouched = fn;
  }

  switch() {
    this.value = !this.value;
    this.switchClicked.emit(this.value);
  }
}
