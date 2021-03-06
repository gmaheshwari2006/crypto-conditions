package com.ripple.cryptoconditions;

/*-
 * ========================LICENSE_START=================================
 * Crypto Conditions
 * %%
 * Copyright (C) 2016 - 2018 Ripple Labs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import com.ripple.cryptoconditions.utils.HashUtils;
import org.immutables.value.Value;

import java.util.Base64;
import java.util.Objects;

/**
 * An implementation of {@link Fulfillment} for a crypto-condition type "PREIMAGE-SHA-256" based upon a preimage and the
 * SHA-256 hash function.
 *
 * @see "https://datatracker.ietf.org/doc/draft-thomas-crypto-conditions/"
 */
public interface PreimageSha256Fulfillment extends Fulfillment<PreimageSha256Condition> {

  /**
   * Constructs an instance of <tt>PreimageSha256Fulfillment</tt> with an associated preimage.
   *
   * @param preimage The preimage associated with the fulfillment.
   *
   * @return A newly created, immutable instance of {@link PreimageSha256Fulfillment}.
   */
  static PreimageSha256Fulfillment from(final byte[] preimage) {
    Objects.requireNonNull(preimage);
    final String encodedPreimage = Base64.getUrlEncoder().encodeToString(preimage);

    final long cost = AbstractPreimageSha256Fulfillment.calculateCost(preimage);
    final byte[] fingerprint = HashUtils.hashFingerprintContents(
        AbstractPreimageSha256Fulfillment.constructFingerprint(preimage)
    );
    final PreimageSha256Condition condition = PreimageSha256Condition.fromCostAndFingerprint(
        cost, fingerprint
    );

    return ImmutablePreimageSha256Fulfillment.builder()
        .type(CryptoConditionType.PREIMAGE_SHA256)
        .encodedPreimage(encodedPreimage)
        .derivedCondition(condition)
        .build();
  }

  /**
   * Accessor for this fulfillment's preimage, using Base64URL encoding.
   *
   * @return A {@link String} containing the base64Url-encoded preimage.
   */
  String getEncodedPreimage();

  /**
   * An abstract implementation of {@link PreimageSha256Fulfillment} for use by the
   * <tt>immutables</tt> library.
   *
   * @see "https://immutables.github.org"
   */
  @Value.Immutable
  abstract class AbstractPreimageSha256Fulfillment implements PreimageSha256Fulfillment {

    /**
     * <p>Constructs the fingerprint for this condition.</p>
     *
     * <p>Note: This method is package-private as (opposed to private) for testing purposes.</p>
     *
     * @param preimage An instance of byte array containing encodedPreimage data.
     */
    static byte[] constructFingerprint(final byte[] preimage) {
      return Objects.requireNonNull(preimage);
    }

    /**
     * Calculates the cost from this condition, which is simply the length from the encodedPreimage.
     *
     * @param preimage The encodedPreimage associated with this condition.
     *
     * @return The cost from a condition based on the encodedPreimage.
     */
    static long calculateCost(byte[] preimage) {
      return preimage.length;
    }

    @Override
    public final boolean verify(final Condition condition, final byte[] message) {
      Objects.requireNonNull(condition,
          "Can't verify a PreimageSha256Fulfillment against an null condition.");
      Objects.requireNonNull(message, "Message must not be null!");

      return getDerivedCondition().equals(condition);
    }
  }
}
