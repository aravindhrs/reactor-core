/*
 * Copyright (c) 2011-2018 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package reactor.core.publisher;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.Fuseable;

import static org.assertj.core.api.Assertions.assertThat;

public class LiftFunctionTest {

	@Test
	public void liftMono() {
		Mono<Integer> source = Mono.just(1)
		                           .hide();

		Operators.LiftFunction<Integer, Integer> liftFunction =
				Operators.LiftFunction.liftScannable(null, (s, actual) -> actual);
		Publisher<Integer> liftOperator = liftFunction.apply(source);

		assertThat(liftOperator)
				.isInstanceOf(Mono.class)
				.isExactlyInstanceOf(MonoLift.class);
	}

	@Test
	public void liftFlux() {
		Flux<Integer> source = Flux.just(1)
		                           .hide();

		Operators.LiftFunction<Integer, Integer> liftFunction =
				Operators.LiftFunction.liftScannable(null, (s, actual) -> actual);
		Publisher<Integer> liftOperator = liftFunction.apply(source);

		assertThat(liftOperator)
				.isInstanceOf(Flux.class)
				.isExactlyInstanceOf(FluxLift.class);
	}

	@Test
	public void liftParallelFlux() {
		ParallelFlux<Integer> source = Flux.just(1)
		                                   .parallel(2)
		                                   .hide();

		Operators.LiftFunction<Integer, Integer> liftFunction =
				Operators.LiftFunction.liftScannable(null, (s, actual) -> actual);
		Publisher<Integer> liftOperator = liftFunction.apply(source);

		assertThat(liftOperator)
				.isInstanceOf(ParallelFlux.class)
				.isExactlyInstanceOf(ParallelLift.class);
	}

	@Test
	public void liftConnectableFlux() {
		ConnectableFlux<Integer> source = Flux.just(1)
		                                      .publish(); //TODO hide if ConnectableFlux gets a hide function

		Operators.LiftFunction<Integer, Integer> liftFunction =
				Operators.LiftFunction.liftScannable(null, (s, actual) -> actual);
		Publisher<Integer> liftOperator = liftFunction.apply(source);

		assertThat(liftOperator)
				.isInstanceOf(ConnectableFlux.class)
				.isExactlyInstanceOf(ConnectableLift.class);
	}

	@Ignore("GroupedFlux is always fuseable for now")
	@Test
	public void liftGroupedFlux() {
		Flux<GroupedFlux<String, Integer>> sourceGroups = Flux
				.just(1)
				.groupBy(i -> "" + i);

		Operators.LiftFunction<Integer, Integer> liftFunction =
				Operators.LiftFunction.liftScannable(null, (s, actual) -> actual);

		sourceGroups.map(g -> liftFunction.apply(g)) //TODO hide if GroupedFlux gets a proper hide() function
		            .doOnNext(liftOperator -> assertThat(liftOperator)
				            .isInstanceOf(GroupedFlux.class)
				            .isExactlyInstanceOf(GroupedLift.class))
		            .blockLast();
	}

	@Test
	public void liftMonoFuseable() {
		Mono<Integer> source = Mono.just(1);

		Operators.LiftFunction<Integer, Integer> liftFunction =
				Operators.LiftFunction.liftScannable(null, (s, actual) -> actual);
		Publisher<Integer> liftOperator = liftFunction.apply(source);

		assertThat(liftOperator)
				.isInstanceOf(Mono.class)
				.isInstanceOf(Fuseable.class)
				.isExactlyInstanceOf(MonoLiftFuseable.class);
	}

	@Test
	public void liftFluxFuseable() {
		Flux<Integer> source = Flux.just(1);

		Operators.LiftFunction<Integer, Integer> liftFunction =
				Operators.LiftFunction.liftScannable(null, (s, actual) -> actual);
		Publisher<Integer> liftOperator = liftFunction.apply(source);

		assertThat(liftOperator)
				.isInstanceOf(Flux.class)
				.isInstanceOf(Fuseable.class)
				.isExactlyInstanceOf(FluxLiftFuseable.class);
	}

	@Ignore("Need a fuseable ParallelFlux")
	@Test
	public void liftParallelFluxFuseable() {
//		ParallelFlux<Integer> source = Flux.just(1)
//		                                   .parallel(2)
	//FIXME use collect?
//		                                   .collect(() -> new ArrayList<Integer>(), (c, v) -> c.add(v));
//
//		Operators.LiftFunction<Integer, Integer> liftFunction =
//				new Operators.LiftFunction<>(null, (s, actual) -> actual);
//		Publisher<Integer> liftOperator = liftFunction.apply(source);
//
//		assertThat(liftOperator)
//				.isInstanceOf(ParallelFlux.class)
//				.isExactlyInstanceOf(ParallelLiftFuseable.class);
	}

	@Test
	public void liftConnectableFluxFuseable() {
		ConnectableFlux<Integer> source = Flux.just(1)
		                                      .publish()
		                                      .replay(2);

		Operators.LiftFunction<Integer, Integer> liftFunction =
				Operators.LiftFunction.liftScannable(null, (s, actual) -> actual);
		Publisher<Integer> liftOperator = liftFunction.apply(source);

		assertThat(liftOperator)
				.isInstanceOf(ConnectableFlux.class)
				.isInstanceOf(Fuseable.class)
				.isExactlyInstanceOf(ConnectableLiftFuseable.class);
	}

	@Test
	public void liftGroupedFluxFuseable() {
		Flux<GroupedFlux<String, Integer>> sourceGroups = Flux
				.just(1)
				.groupBy(i -> "" + i);

		Operators.LiftFunction<Integer, Integer> liftFunction =
				Operators.LiftFunction.liftScannable(null, (s, actual) -> actual);

		sourceGroups.map(g -> liftFunction.apply(g))
		            .doOnNext(liftOperator -> assertThat(liftOperator)
				            .isInstanceOf(GroupedFlux.class)
				            .isInstanceOf(Fuseable.class)
				            .isExactlyInstanceOf(GroupedLiftFuseable.class))
		            .blockLast();
	}
}